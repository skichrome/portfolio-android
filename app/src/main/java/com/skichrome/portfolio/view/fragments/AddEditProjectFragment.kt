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
    private var projectPhotoPath: Uri? = null
    private var remotePhotoPath: String? = null
    private var lastParagraphIndex: Int? = null
    private var lastParagraphPhotoPath: Uri? = null

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
        when (requestCode)
        {
            RC_IMAGE_CAPTURE_PROJECTS_INTENT ->
            {
                if (resultCode == RESULT_OK)
                    projectPhotoPath?.let { binding.addEditProjectFragmentImg.loadPhotoWithGlide(it) }
            }
            RC_IMAGE_CAPTURE_PARAGRAPHS_INTENT ->
            {
                if (resultCode == RESULT_OK && lastParagraphIndex != null && lastParagraphPhotoPath != null)
                    viewModel.updateParagraphPicture(lastParagraphIndex!!, lastParagraphPhotoPath!!)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        outState.putString(CURRENT_PROJECT_PICTURE_PATH_REF, projectPhotoPath.toString())
        outState.putString(CURRENT_REMOTE_PROJECT_PICTURE_PATH_REF, remotePhotoPath)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?)
    {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.getString(CURRENT_REMOTE_PROJECT_PICTURE_PATH_REF)?.let { remotePhotoPath = it }
        savedInstanceState?.getString(CURRENT_PROJECT_PICTURE_PATH_REF)?.let { projectPhotoPath = Uri.parse(it) }

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
        viewModel.paragraphLongClickEvent.observe(viewLifecycleOwner, EventObserver { toast("Paragraph long click") })
        viewModel.paragraphPictureClickEvent.observe(viewLifecycleOwner, EventObserver {
            lastParagraphPhotoPath = launchCamera(RC_IMAGE_CAPTURE_PARAGRAPHS_INTENT, PICTURES_PROJECT_FOLDER_NAME)
            lastParagraphIndex = it
        })
        viewModel.project.observe(viewLifecycleOwner, Observer {
            it?.let { project ->
                projectCreationDate = project.createdAt
                project.mainPicture?.let { imgRef ->
                    remotePhotoPath = imgRef
                    binding.addEditProjectFragmentImg.loadPhotoWithGlide(imgRef)
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
                content = mutableListOf(ParagraphContent())
            )
        )
    }

    private fun configureBinding()
    {
        binding.viewModel = viewModel
        binding.addEditProjectFragmentImg.setOnClickListener {
            projectPhotoPath = launchCamera(RC_IMAGE_CAPTURE_PROJECTS_INTENT, PICTURES_PROJECT_FOLDER_NAME)
        }
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
        activity?.run {
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

            toolbar?.title = args.projectId?.let {
                getString(R.string.navigation_fragment_title_edit_project_form)
            } ?: getString(R.string.navigation_fragment_title_new_project_form)
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
                mainPictureAlt = binding.addEditProjectFragmentImgAltEditText.text.toString(),
                localMainPicture = projectPhotoPath
            )
            viewModel.saveProject(args.themeId, args.categoryId, args.projectId, newProject)
        }
        else
            binding.root.snackBar(getString(R.string.add_edit_project_fragment_required_field_msg_snack_bar))
    }
}