package com.skichrome.portfolio.view.fragments

import android.app.Activity
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
import com.skichrome.portfolio.databinding.FragmentAddEditThemesBinding
import com.skichrome.portfolio.model.remote.util.Theme
import com.skichrome.portfolio.util.*
import com.skichrome.portfolio.viewmodel.AddEditThemesViewModel
import com.skichrome.portfolio.viewmodel.AddEditThemesViewModelFactory
import kotlinx.android.synthetic.main.toolbar.*

class AddEditThemesFragment : Fragment()
{
    // =================================
    //              Fields
    // =================================

    private lateinit var binding: FragmentAddEditThemesBinding
    private lateinit var requiredFields: List<TextInputEditText>
    private val viewModel by viewModels<AddEditThemesViewModel> { AddEditThemesViewModelFactory((requireActivity().application as PortfolioApplication).themesRepository) }
    private val args by navArgs<AddEditThemesFragmentArgs>()
    private var localImagePath: Uri? = null
    private var remoteImagePath: String? = null

    // =================================
    //        Superclass Methods
    // =================================

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        FragmentAddEditThemesBinding.inflate(inflater, container, false).let {
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
            RC_IMAGE_CAPTURE_THEME_INTENT ->
            {
                if (resultCode == Activity.RESULT_OK)
                    localImagePath?.let { binding.addEditThemeFragmentImage.loadPhotoWithGlide(it) }
            }
            RC_IMAGE_PICKER_THEMES ->
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    localImagePath = data?.data
                    localImagePath?.let { binding.addEditThemeFragmentImage.loadPhotoWithGlide(it) }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        outState.putString(CURRENT_THEME_PICTURE_PATH_REF, localImagePath.toString())
        outState.putString(CURRENT_REMOTE_THEME_PICTURE_PATH_REF, remoteImagePath)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?)
    {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.getString(CURRENT_THEME_PICTURE_PATH_REF)?.let { localImagePath = Uri.parse(it) }
        savedInstanceState?.getString(CURRENT_REMOTE_THEME_PICTURE_PATH_REF)?.let { remoteImagePath = it }

        remoteImagePath?.let {
            binding.addEditThemeFragmentImage.loadPhotoWithGlide(it)
        } ?: localImagePath?.let {
            binding.addEditThemeFragmentImage.loadPhotoWithGlide(it)
        }
    }

    // =================================
    //              Methods
    // =================================

    private fun configureViewModel()
    {
        viewModel.message.observe(viewLifecycleOwner, EventObserver { binding.root.snackBar(getString(it)) })
        viewModel.theme.observe(viewLifecycleOwner, Observer {
            it?.let {
                remoteImagePath = it.imgReference
                remoteImagePath?.let { path -> binding.addEditThemeFragmentImage.loadPhotoWithGlide(path) }
            }
        })
        viewModel.loading.observe(viewLifecycleOwner, Observer {
            if (it == false)
                findNavController().navigateUp()
        })
        args.themeId?.let { viewModel.loadTheme(it) }
    }

    private fun configureBinding()
    {
        binding.viewModel = viewModel
        binding.addEditThemeFragmentUpdateBtnPicker.setOnClickListener { openImagePicker(binding.root, RC_IMAGE_PICKER_THEMES) }
        binding.addEditThemeFragmentUpdateBtnCamera.setOnClickListener {
            localImagePath = launchCamera(RC_IMAGE_CAPTURE_THEME_INTENT, PICTURES_THEME_FOLDER_NAME)
        }
    }

    private fun configureUI()
    {
        requiredFields = listOf(
            binding.addEditThemeFragmentNameEditText,
            binding.addEditThemeFragmentDescriptionEditText,
            binding.addEditThemeFragmentImgAltEditText
        )

        activity?.run {
            toolbar?.title = args.themeId?.let {
                getString(R.string.navigation_fragment_title_edit_theme_form)
            } ?: getString(R.string.navigation_fragment_title_new_theme_form)
        }
    }

    private fun configureFAB()
    {
        binding.addEditThemeFragmentFab.setOnClickListener { saveData() }
    }

    private fun saveData()
    {
        var canSave = true
        requiredFields.forEach { textView ->
            if (textView.text.toString() == "")
            {
                textView.error = getString(R.string.add_edit_theme_fragment_required_field_msg)
                canSave = false
                return@forEach
            }
        }

        if (localImagePath == null)
        {
            binding.root.snackBar(getString(R.string.add_edit_theme_fragment_required_field_msg_no_picture_set))
            return
        }

        if (canSave)
        {
            val theme = Theme(
                name = binding.addEditThemeFragmentNameEditText.text.toString(),
                description = binding.addEditThemeFragmentDescriptionEditText.text.toString(),
                imgReferenceAlt = binding.addEditThemeFragmentImgAltEditText.text.toString(),
                imgReference = remoteImagePath,
                localImgReference = localImagePath
            )

            viewModel.saveTheme(theme, args.themeId)
        }
        else
            binding.root.snackBar(getString(R.string.add_edit_theme_fragment_required_field_msg_snack_bar))
    }
}