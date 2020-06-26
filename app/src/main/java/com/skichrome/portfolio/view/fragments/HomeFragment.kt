package com.skichrome.portfolio.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.skichrome.portfolio.PortfolioApplication
import com.skichrome.portfolio.R
import com.skichrome.portfolio.databinding.FragmentHomeBinding
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
        DataBindingUtil.inflate<FragmentHomeBinding>(inflater, R.layout.fragment_home, container, false).let {
            binding = it
            return@let binding.root
        }
}