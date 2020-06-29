package com.skichrome.portfolio.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.skichrome.portfolio.PortfolioApplication
import com.skichrome.portfolio.databinding.FragmentProjectsBinding
import com.skichrome.portfolio.util.AutoClearedValue
import com.skichrome.portfolio.util.EventObserver
import com.skichrome.portfolio.util.snackBar
import com.skichrome.portfolio.util.toast
import com.skichrome.portfolio.view.ui.ProjectsAdapter
import com.skichrome.portfolio.viewmodel.ProjectsViewModel
import com.skichrome.portfolio.viewmodel.ProjectsViewModelFactory
import kotlinx.android.synthetic.main.fragment_projects.*

class ProjectsFragment : Fragment()
{
    // =================================
    //              Fields
    // =================================

    private lateinit var binding: FragmentProjectsBinding
    private val viewModel by viewModels<ProjectsViewModel> { ProjectsViewModelFactory((requireActivity().application as PortfolioApplication).projectsRepository) }
    private val args by navArgs<ProjectsFragmentArgs>()
    private var adapter by AutoClearedValue<ProjectsAdapter>()

    // =================================
    //        Superclass Methods
    // =================================

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        FragmentProjectsBinding.inflate(inflater, container, false).let {
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

    // =================================
    //              Methods
    // =================================

    private fun configureViewModel()
    {
        viewModel.message.observe(viewLifecycleOwner, EventObserver { binding.root.snackBar(getString(it)) })
        viewModel.projectClickEvent.observe(viewLifecycleOwner, EventObserver { navigateToAddEditProjectFragment(it) })
        viewModel.projectLongClickEvent.observe(viewLifecycleOwner, EventObserver { toast("Long clic Not implemented") })
        viewModel.loadCategories(args.themeId, args.categoryId)
    }

    private fun configureBinding()
    {
        binding.viewModel = viewModel
    }

    private fun configureRecyclerView()
    {
        adapter = ProjectsAdapter(viewModel)
        binding.projectsFragmentRecyclerView.adapter = adapter
    }

    private fun configureFAB()
    {
        projects_fragment_fab.setOnClickListener { navigateToAddEditProjectFragment() }
    }

    private fun navigateToAddEditProjectFragment(projectId: String? = null)
    {
        val opts = ProjectsFragmentDirections.actionProjectsFragmentToAddEditProjectFragment(args.themeId, args.categoryId, projectId)
        findNavController().navigate(opts)
    }
}
