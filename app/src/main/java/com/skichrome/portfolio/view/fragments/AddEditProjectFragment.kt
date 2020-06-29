package com.skichrome.portfolio.view.fragments

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
import com.skichrome.portfolio.util.AutoClearedValue
import com.skichrome.portfolio.util.EventObserver
import com.skichrome.portfolio.util.snackBar
import com.skichrome.portfolio.util.toast
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

    // =================================
    //              Methods
    // =================================

    private fun configureViewModel()
    {
        viewModel.message.observe(viewLifecycleOwner, EventObserver { binding.root.snackBar(getString(it)) })
        viewModel.paragraphClickEvent.observe(viewLifecycleOwner, EventObserver { toast("Paragraph click") })
        viewModel.paragraphLongClickEvent.observe(viewLifecycleOwner, EventObserver { toast("Paragraph long click") })
        viewModel.project.observe(viewLifecycleOwner, Observer { it?.let { project -> projectCreationDate = project.createdAt } })
        viewModel.loading.observe(viewLifecycleOwner, Observer {
            if (it == false)
                findNavController().navigateUp()
        })

        args.projectId?.let {
            viewModel.loadProject(args.themeId, args.categoryId, it)
        } ?: viewModel.initNewProject(Project(title = "", createdAt = projectCreationDate, content = mutableListOf(ParagraphContent().withId("0"))))
    }

    private fun configureBinding()
    {
        binding.viewModel = viewModel
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

        if (canRegisterData)
        {
            val newProject = Project(
                title = binding.addEditProjectFragmentProjectTitleEditText.text.toString(),
                createdAt = projectCreationDate,
                mainPicture = null,
                mainPictureAlt = binding.addEditProjectFragmentImgAltEditText.text.toString()
            )
            viewModel.saveProject(args.themeId, args.categoryId, args.projectId, newProject)
        }
        else
            binding.root.snackBar(getString(R.string.add_edit_project_fragment_required_field_msg_snack_bar))
    }
}