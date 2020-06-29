package com.skichrome.portfolio.view.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
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
import androidx.navigation.ui.onNavDestinationSelected
import com.firebase.ui.auth.AuthUI
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.skichrome.portfolio.PortfolioApplication
import com.skichrome.portfolio.R
import com.skichrome.portfolio.databinding.FragmentAddEditProjectBinding
import com.skichrome.portfolio.model.remote.util.ParagraphContent
import com.skichrome.portfolio.model.remote.util.Project
import com.skichrome.portfolio.util.*
import com.skichrome.portfolio.view.ui.ProjectParagraphContentAdapter
import com.skichrome.portfolio.viewmodel.AddEditProjectViewModel
import com.skichrome.portfolio.viewmodel.AddEditProjectViewModelFactory
import kotlinx.android.synthetic.main.toolbar.*
import java.io.IOException

class AddEditProjectFragment : Fragment()
{
    // =================================
    //              Fields
    // =================================

    private lateinit var binding: FragmentAddEditProjectBinding
    private lateinit var requiredFields: List<TextInputEditText>
    private val viewModel by viewModels<AddEditProjectViewModel> { AddEditProjectViewModelFactory((requireActivity().application as PortfolioApplication).projectsRepository) }
    private val args by navArgs<AddEditProjectFragmentArgs>()
    private var adapter by AutoClearedValue<ProjectParagraphContentAdapter>()
    private var projectCreationDate = Timestamp(System.currentTimeMillis() / 1000, 0)
    private var projectPhotoPath: String? = null
    private var remotePhotoPath: String? = null

    // =================================
    //        Superclass Methods
    // =================================

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        FragmentAddEditProjectBinding.inflate(inflater, container, false).let {
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
        configureRecyclerView()
        configureFAB()
    }

    override fun onResume()
    {
        super.onResume()
        configureUI(true)
    }

    override fun onPause()
    {
        configureUI(false)
        super.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_IMAGE_CAPTURE_INTENT)
        {
            if (resultCode == RESULT_OK)
            {
                projectPhotoPath?.let {
                    binding.addEditProjectFragmentImg.loadPhotoWithGlide(it)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        outState.putString(CURRENT_PICTURE_PATH_REF, projectPhotoPath)
        outState.putString(CURRENT_REMOTE_PICTURE_PATH_REF, projectPhotoPath)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?)
    {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.getString(CURRENT_REMOTE_PICTURE_PATH_REF)?.let { remotePhotoPath = it }
        savedInstanceState?.getString(CURRENT_PICTURE_PATH_REF)?.let { projectPhotoPath = it }

        remotePhotoPath?.let {
            binding.addEditProjectFragmentImg.loadPhotoWithGlide(it)
        } ?: projectPhotoPath?.let {
            binding.addEditProjectFragmentImg.loadPhotoWithGlide(it)
        }
    }

    // =================================
    //              Methods
    // =================================

    private fun configureViewModel()
    {
        viewModel.message.observe(viewLifecycleOwner, EventObserver { binding.root.snackBar(getString(it)) })
        viewModel.paragraphClickEvent.observe(viewLifecycleOwner, EventObserver { toast("Paragraph click") })
        viewModel.paragraphLongClickEvent.observe(viewLifecycleOwner, EventObserver { toast("Paragraph long click") })
        viewModel.project.observe(viewLifecycleOwner, Observer {
            it?.let { project ->
                projectCreationDate = project.createdAt
                project.mainPicture?.let { imgRef ->
                    remotePhotoPath = imgRef
                    binding.addEditProjectFragmentImg.loadPhotoWithGlide(Uri.parse(imgRef))
                }
            }
        })
        viewModel.loading.observe(viewLifecycleOwner, Observer {
            if (it == false)
                findNavController().navigateUp()
        })

        args.projectId?.let {
            viewModel.loadProject(args.themeId, args.categoryId, it)
        } ?: viewModel.initNewProject(
            Project(
                title = "",
                description = "",
                createdAt = projectCreationDate,
                content = mutableListOf(ParagraphContent().withId("0"))
            )
        )
    }

    private fun configureBinding()
    {
        binding.viewModel = viewModel
        binding.addEditProjectFragmentImg.setOnClickListener { launchCamera() }
    }

    private fun configureRecyclerView()
    {
        adapter = ProjectParagraphContentAdapter(viewModel)
        binding.addEditProjectFragmentRecyclerView.adapter = adapter
    }

    private fun configureFAB()
    {
        binding.addEditProjectFragmentFab.setOnClickListener { saveData() }
    }

    private fun configureUI(menuItemVisibility: Boolean = true)
    {
        activity?.apply {
            toolbar?.menu?.findItem(R.id.activity_main_menu_new_paragraph)?.isVisible = menuItemVisibility
            toolbar?.setOnMenuItemClickListener {
                when (it.itemId)
                {
                    R.id.activity_main_menu_logout ->
                    {
                        AuthUI.getInstance().signOut(this)
                        this.finish()
                    }
                    R.id.activity_main_menu_new_paragraph ->
                    {
                        if (menuItemVisibility)
                            viewModel.addNewParagraph()
                        else
                            return@setOnMenuItemClickListener true

                    }
                    else -> return@setOnMenuItemClickListener it.onNavDestinationSelected(findNavController()) || super.onOptionsItemSelected(it)
                }
                return@setOnMenuItemClickListener true
            }
        }

        requiredFields = listOf(
            binding.addEditProjectFragmentProjectTitleEditText,
            binding.addEditProjectFragmentProjectDescriptionEditText,
            binding.addEditProjectFragmentImgAltEditText
        )
    }

    private fun saveData()
    {
        var canRegisterData = true

        requiredFields.forEach { textView ->
            if (textView.text.toString() == "")
            {
                textView.error = getString(R.string.add_edit_project_fragment_required_field_msg)
                canRegisterData = false
                return@forEach
            }
        }

        if (projectPhotoPath == null && remotePhotoPath == null)
        {
            binding.root.snackBar(getString(R.string.add_edit_project_fragment_required_field_msg_no_picture_set))
            return
        }

        if (canRegisterData)
        {
            val newProject = Project(
                title = binding.addEditProjectFragmentProjectTitleEditText.text.toString(),
                description = binding.addEditProjectFragmentProjectDescriptionEditText.text.toString(),
                createdAt = projectCreationDate,
                mainPicture = remotePhotoPath,
                mainPictureAlt = binding.addEditProjectFragmentImgAltEditText.text.toString()
            )
            viewModel.saveProject(args.themeId, args.categoryId, args.projectId, newProject, projectPhotoPath)
        }
        else
            binding.root.snackBar(getString(R.string.add_edit_project_fragment_required_field_msg_snack_bar))
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
                            ?.createOrGetJpegFile(PICTURES_FOLDER_NAME, "projects")
                    }
                    else null
                }
                catch (e: IOException)
                {
                    errorLog("Error when getting photo file : ${e.message}", e)
                    null
                }

                photoFile?.also { file ->
                    projectPhotoPath = file.absolutePath
                    val uri = FileProvider.getUriForFile(
                        requireActivity().applicationContext,
                        requireActivity().getString(R.string.file_provider_authority),
                        file
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(takePictureIntent, RC_IMAGE_CAPTURE_INTENT)
                }
            }
        }
    }
}