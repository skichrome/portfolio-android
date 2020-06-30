package com.skichrome.portfolio.view.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
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
import java.io.IOException

class AddEditThemesFragment : Fragment()
{
    // =================================
    //              Fields
    // =================================

    private lateinit var binding: FragmentAddEditThemesBinding
    private lateinit var requiredFields: List<TextInputEditText>
    private val viewModel by viewModels<AddEditThemesViewModel> { AddEditThemesViewModelFactory((requireActivity().application as PortfolioApplication).themesRepository) }
    private val args by navArgs<AddEditThemesFragmentArgs>()
    private var localImagePath: String? = null
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
        }
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        outState.putString(CURRENT_PROJECT_PICTURE_PATH_REF, localImagePath)
        outState.putString(CURRENT_REMOTE_CATEGORY_PICTURE_PATH_REF, remoteImagePath)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?)
    {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.getString(CURRENT_CATEGORY_PICTURE_PATH_REF)?.let { localImagePath = it }
        savedInstanceState?.getString(CURRENT_REMOTE_CATEGORY_PICTURE_PATH_REF)?.let { remoteImagePath = it }

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
        binding.addEditThemeFragmentImage.setOnClickListener { launchCamera() }
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

    private fun launchCamera()
    {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                val photoFile = try
                {
                    if (canWriteExternalStorage())
                    {
                        requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                            ?.createOrGetJpegFile(PICTURES_THEME_FOLDER_NAME, "category")
                    }
                    else null
                }
                catch (e: IOException)
                {
                    errorLog("Error when getting photo file : ${e.message}", e)
                    null
                }

                photoFile?.also { file ->
                    localImagePath = file.absolutePath
                    val uri = FileProvider.getUriForFile(
                        requireActivity().applicationContext,
                        requireActivity().getString(R.string.file_provider_authority),
                        file
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(takePictureIntent, RC_IMAGE_CAPTURE_THEME_INTENT)
                }
            }
        }
    }
}