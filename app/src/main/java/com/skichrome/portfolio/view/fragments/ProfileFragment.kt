package com.skichrome.portfolio.view.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.skichrome.portfolio.PortfolioApplication
import com.skichrome.portfolio.R
import com.skichrome.portfolio.databinding.FragmentProfileBinding
import com.skichrome.portfolio.model.remote.util.User
import com.skichrome.portfolio.util.*
import com.skichrome.portfolio.viewmodel.ProfileViewModel
import com.skichrome.portfolio.viewmodel.ProfileViewModelFactory

class ProfileFragment : Fragment()
{
    // =================================
    //              Fields
    // =================================

    private lateinit var binding: FragmentProfileBinding
    private lateinit var requiredFields: List<TextInputEditText>
    private val viewModel by viewModels<ProfileViewModel> { ProfileViewModelFactory((requireActivity().application as PortfolioApplication).homeRepository) }
    private val args by navArgs<ProfileFragmentArgs>()
    private var localImageRef: Uri? = null
    private var remoteImageRef: String? = null

    // =================================
    //        Superclass Methods
    // =================================

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        FragmentProfileBinding.inflate(inflater, container, false).let {
            binding = it
            binding.lifecycleOwner = this.viewLifecycleOwner
            binding.executePendingBindings()
            return@let binding.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        configureViewModel()
        configureBinding()
        configureUI()
        configureFAB()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode)
        {
            RC_IMAGE_CAPTURE_USER_INTENT ->
            {
                if (resultCode == RESULT_OK)
                    localImageRef?.let { binding.profileFragmentUserImg.loadPhotoWithGlide(it) }
            }
            RC_IMAGE_PICKER_USERS ->
            {
                if (resultCode == RESULT_OK)
                {
                    localImageRef = data?.data
                    localImageRef?.let { binding.profileFragmentUserImg.loadPhotoWithGlide(it) }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        outState.putString(CURRENT_USER_PICTURE_PATH_REF, localImageRef.toString())
        outState.putString(CURRENT_REMOTE_USER_PICTURE_PATH_REF, remoteImageRef)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?)
    {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.getString(CURRENT_USER_PICTURE_PATH_REF)?.let { localImageRef = Uri.parse(it) }
        savedInstanceState?.getString(CURRENT_REMOTE_USER_PICTURE_PATH_REF)?.let { remoteImageRef = it }

        remoteImageRef?.let {
            binding.profileFragmentUserImg.loadPhotoWithGlide(it)
        } ?: localImageRef?.let {
            binding.profileFragmentUserImg.loadPhotoWithGlide(it)
        }
    }

    // =================================
    //              Methods
    // =================================

    private fun configureViewModel()
    {
        viewModel.message.observe(viewLifecycleOwner, EventObserver { binding.root.snackBar(getString(it)) })
        viewModel.user.observe(viewLifecycleOwner, Observer {
            remoteImageRef = it.photoReference
            remoteImageRef?.let { path -> binding.profileFragmentUserImg.loadPhotoWithGlide(path) }
        })
        viewModel.loading.observe(viewLifecycleOwner, Observer {
            if (it == false)
                findNavController().navigateUp()
        })
        args.userId?.let {
            viewModel.loadUserInfo(it)
        }
    }

    private fun configureBinding()
    {
        binding.viewModel = viewModel
        binding.profileFragmentImgUpdateBtnCamera.setOnClickListener {
            localImageRef = launchCamera(RC_IMAGE_CAPTURE_USER_INTENT, PICTURES_USERS_FOLDER_NAME)
        }
        binding.profileFragmentImgUpdateBtnPicker.setOnClickListener { openImagePicker(binding.root, RC_IMAGE_PICKER_USERS) }
    }

    private fun configureUI()
    {
        requiredFields = listOf(
            binding.profileFragmentUserFirstNameEdit,
            binding.profileFragmentUserLastNameEdit,
            binding.profileFragmentUserEmailEdit,
            binding.profileFragmentUserPhoneEdit,
            binding.profileFragmentUserSummaryEdit
        )
    }

    private fun configureFAB()
    {
        binding.profileFragmentFab.setOnClickListener { saveData() }
    }

    private fun saveData()
    {
        var canSave = true

        requiredFields.forEach { textView ->
            if (textView.text.toString() == "")
            {
                textView.error = getString(R.string.edit_profile_fragment_required_field_msg)
                canSave = false
                return@forEach
            }
        }

        if (localImageRef == null)
        {
            binding.root.snackBar(getString(R.string.edit_profile_fragment_required_field_msg_no_picture_set))
            return
        }

        if (canSave)
        {
            val user = User(
                firstName = binding.profileFragmentUserFirstNameEdit.text.toString(),
                lastName = binding.profileFragmentUserLastNameEdit.text.toString(),
                phoneNumber = binding.profileFragmentUserPhoneEdit.text.toString(),
                email = binding.profileFragmentUserEmailEdit.text.toString(),
                summary = binding.profileFragmentUserSummaryEdit.text.toString(),
                localPhotoReference = localImageRef,
                photoReference = remoteImageRef
            )
            viewModel.uploadUser(user, args.userId)
        }
        else
            binding.root.snackBar(getString(R.string.edit_profile_fragment_required_field_msg_snack_bar))
    }
}