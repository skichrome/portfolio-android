package com.skichrome.portfolio.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.skichrome.portfolio.PortfolioApplication
import com.skichrome.portfolio.databinding.FragmentThemesBinding
import com.skichrome.portfolio.util.AutoClearedValue
import com.skichrome.portfolio.util.EventObserver
import com.skichrome.portfolio.util.snackBar
import com.skichrome.portfolio.view.ui.ThemesAdapter
import com.skichrome.portfolio.viewmodel.ThemesViewModel
import com.skichrome.portfolio.viewmodel.ThemesViewModelFactory

class ThemesFragment : Fragment()
{
    // =================================
    //              Fields
    // =================================

    private lateinit var binding: FragmentThemesBinding
    private val viewModel by viewModels<ThemesViewModel> { ThemesViewModelFactory((requireActivity().application as PortfolioApplication).themesRepository) }
    private var adapter by AutoClearedValue<ThemesAdapter>()

    // =================================
    //        Superclass Methods
    // =================================

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        FragmentThemesBinding.inflate(inflater, container, false).let {
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
        viewModel.errorMsgReference.observe(viewLifecycleOwner, EventObserver { binding.root.snackBar(getString(it)) })
    }

    private fun configureBinding()
    {
        binding.viewModel = viewModel
    }

    private fun configureRecyclerView()
    {
        adapter = ThemesAdapter(viewModel)
        binding.themeFragmentRecyclerView.adapter = adapter
    }

    private fun configureFAB()
    {
    }
}
