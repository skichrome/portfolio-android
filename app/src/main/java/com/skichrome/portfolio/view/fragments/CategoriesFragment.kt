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
import com.skichrome.portfolio.databinding.FragmentCategoriesBinding
import com.skichrome.portfolio.util.AutoClearedValue
import com.skichrome.portfolio.util.EventObserver
import com.skichrome.portfolio.util.snackBar
import com.skichrome.portfolio.util.toast
import com.skichrome.portfolio.view.ui.CategoriesAdapter
import com.skichrome.portfolio.viewmodel.CategoriesViewModel
import com.skichrome.portfolio.viewmodel.CategoriesViewModelFactory

class CategoriesFragment : Fragment()
{
    // =================================
    //              Fields
    // =================================

    private lateinit var binding: FragmentCategoriesBinding
    private val viewModel by viewModels<CategoriesViewModel> { CategoriesViewModelFactory((requireActivity().application as PortfolioApplication).categoriesRepository) }
    private val args by navArgs<CategoriesFragmentArgs>()
    private var adapter by AutoClearedValue<CategoriesAdapter>()

    // =================================
    //        Superclass Methods
    // =================================

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        FragmentCategoriesBinding.inflate(inflater, container, false).let {
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
        configureBtn()
    }

    // =================================
    //              Methods
    // =================================

    private fun configureViewModel()
    {
        viewModel.errorMsgReference.observe(viewLifecycleOwner, EventObserver { binding.root.snackBar(getString(it)) })
        viewModel.categoryClickEvent.observe(viewLifecycleOwner, EventObserver { navigateToProjectsFragment(it) })
        viewModel.categoryLongClickEvent.observe(viewLifecycleOwner, EventObserver { editOrDeleteCategory(it) })
        viewModel.loadCategories(args.themeId)
    }

    private fun configureBinding()
    {
        binding.viewModel = viewModel
    }

    private fun configureRecyclerView()
    {
        adapter = CategoriesAdapter(viewModel)
        binding.categoriesFragmentRecyclerView.adapter = adapter
    }

    private fun configureBtn()
    {
    }

    private fun editOrDeleteCategory(categoryId: String)
    {
        toast("Feature not implemented, theme id : $categoryId")
    }

    private fun navigateToProjectsFragment(categoryId: String)
    {
        val opts = CategoriesFragmentDirections.actionCategoriesFragmentToProjectsFragment(args.themeId, categoryId)
        findNavController().navigate(opts)
    }
}