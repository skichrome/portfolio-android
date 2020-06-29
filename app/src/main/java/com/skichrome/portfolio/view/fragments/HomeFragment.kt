package com.skichrome.portfolio.view.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.skichrome.portfolio.PortfolioApplication
import com.skichrome.portfolio.R
import com.skichrome.portfolio.databinding.FragmentHomeBinding
import com.skichrome.portfolio.util.EventObserver
import com.skichrome.portfolio.util.loadPhotoWithGlide
import com.skichrome.portfolio.util.snackBar
import com.skichrome.portfolio.viewmodel.HomeViewModel
import com.skichrome.portfolio.viewmodel.HomeViewModelFactory

class HomeFragment : Fragment()
{
    // =================================
    //              Fields
    // =================================

    private lateinit var binding: FragmentHomeBinding
    private val viewModel by viewModels<HomeViewModel> { HomeViewModelFactory((requireActivity().application as PortfolioApplication).homeRepository) }

    // =================================
    //        Superclass Methods
    // =================================

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        FragmentHomeBinding.inflate(inflater, container, false).let {
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
        configureBtn()
    }

    // =================================
    //              Methods
    // =================================

    private fun configureViewModel()
    {
        viewModel.message.observe(viewLifecycleOwner, EventObserver { binding.root.snackBar(getString(it)) })
        viewModel.user.observe(viewLifecycleOwner, Observer { loadUserPicture(it?.photoReference?.let { photoRef -> Uri.parse(photoRef) }) })
        viewModel.loadUserInfo()
    }

    private fun configureBinding()
    {
        binding.viewModel = viewModel
    }

    private fun configureBtn()
    {
        binding.homeFragmentBtnProfile.setOnClickListener { navigateToProfileFragment() }
        binding.homeFragmentBtnProjects.setOnClickListener { navigateToThemesFragment() }
    }

    private fun loadUserPicture(imgReference: Uri?) = imgReference?.let {
        binding.homeFragmentUserImg.loadPhotoWithGlide(it)
    }

    private fun navigateToProfileFragment()
    {
        findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
    }

    private fun navigateToThemesFragment()
    {
        findNavController().navigate(R.id.action_homeFragment_to_themesFragment)
    }
}