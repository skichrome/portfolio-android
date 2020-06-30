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
import com.skichrome.portfolio.databinding.FragmentAddEditCategoryBinding
import com.skichrome.portfolio.model.remote.util.Category
import com.skichrome.portfolio.util.*
import com.skichrome.portfolio.viewmodel.AddEditCategoryViewModel
import com.skichrome.portfolio.viewmodel.AddEditCategoryViewModelFactory
import java.io.IOException

class AddEditCategoryFragment : Fragment()
{
    // =================================
    //              Fields
    // =================================

    private lateinit var binding: FragmentAddEditCategoryBinding
    private lateinit var requiredField: List<TextInputEditText>
    private val viewModel by viewModels<AddEditCategoryViewModel> { AddEditCategoryViewModelFactory((requireActivity().application as PortfolioApplication).categoriesRepository) }
    private val args by navArgs<AddEditCategoryFragmentArgs>()
    private var localImgPath: String? = null
    private var remoteImgPath: String? = null

    // =================================
    //        Superclass Methods
    // =================================

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        FragmentAddEditCategoryBinding.inflate(inflater, container, false).let {
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
            RC_IMAGE_CAPTURE_CATEGORY_INTENT ->
            {
                if (resultCode == Activity.RESULT_OK)
                    localImgPath?.let { binding.addEditCategoryFragmentImage.loadPhotoWithGlide(it) }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        outState.putString(CURRENT_PROJECT_PICTURE_PATH_REF, localImgPath)
        outState.putString(CURRENT_REMOTE_CATEGORY_PICTURE_PATH_REF, remoteImgPath)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?)
    {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.getString(CURRENT_CATEGORY_PICTURE_PATH_REF)?.let { localImgPath = it }
        savedInstanceState?.getString(CURRENT_REMOTE_CATEGORY_PICTURE_PATH_REF)?.let { remoteImgPath = it }

        remoteImgPath?.let {
            binding.addEditCategoryFragmentImage.loadPhotoWithGlide(it)
        } ?: localImgPath?.let {
            binding.addEditCategoryFragmentImage.loadPhotoWithGlide(it)
        }
    }

    // =================================
    //              Methods
    // =================================

    private fun configureViewModel()
    {
        viewModel.message.observe(viewLifecycleOwner, EventObserver { binding.root.snackBar(getString(it)) })
        viewModel.category.observe(viewLifecycleOwner, Observer {
            it?.let { category ->
                remoteImgPath = category.imgReference
                remoteImgPath?.let { path -> binding.addEditCategoryFragmentImage.loadPhotoWithGlide(path) }
            }
        })
        viewModel.loading.observe(viewLifecycleOwner, Observer {
            if (it == false)
                findNavController().navigateUp()
        })
        args.categoryId?.let {
            viewModel.loadCategory(args.themeId, it)
        }
    }

    private fun configureBinding()
    {
        binding.viewModel = viewModel
        binding.addEditCategoryFragmentImage.setOnClickListener { launchCamera() }
    }

    private fun configureUI()
    {
        requiredField = listOf(
            binding.addEditCategoryFragmentNameEditText,
            binding.addEditCategoryFragmentContentEditText,
            binding.addEditCategoryFragmentImgAltEditText
        )
    }

    private fun configureFAB()
    {
        binding.addEditCategoryFragmentFab.setOnClickListener { saveData() }
    }

    private fun saveData()
    {
        var canSave = true

        requiredField.forEach { editText ->
            if (editText.text.toString() == "")
            {
                canSave = false
                editText.error = getString(R.string.add_edit_category_fragment_required_field_msg)
                return@forEach
            }
        }

        if (localImgPath == null)
        {
            binding.root.snackBar(getString(R.string.add_edit_category_fragment_required_field_msg_no_picture_set))
            return
        }

        if (canSave)
        {
            val category = Category(
                name = binding.addEditCategoryFragmentNameEditText.text.toString(),
                description = binding.addEditCategoryFragmentContentEditText.text.toString(),
                imageAlt = binding.addEditCategoryFragmentImgAltEditText.text.toString(),
                imgReference = null,
                localFileReference = localImgPath
            )
            viewModel.saveCategory(args.themeId, category, args.categoryId)
        }
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
                            ?.createOrGetJpegFile(PICTURES_CATEGORY_FOLDER_NAME, "category")
                    }
                    else null
                }
                catch (e: IOException)
                {
                    errorLog("Error when getting photo file : ${e.message}", e)
                    null
                }

                photoFile?.also { file ->
                    localImgPath = file.absolutePath
                    val uri = FileProvider.getUriForFile(
                        requireActivity().applicationContext,
                        requireActivity().getString(R.string.file_provider_authority),
                        file
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(takePictureIntent, RC_IMAGE_CAPTURE_CATEGORY_INTENT)
                }
            }
        }
    }
}