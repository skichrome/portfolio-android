package com.skichrome.portfolio.view.fragments

import android.app.Activity
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
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.skichrome.portfolio.PortfolioApplication
import com.skichrome.portfolio.R
import com.skichrome.portfolio.databinding.FragmentHomeBinding
import com.skichrome.portfolio.util.*
import com.skichrome.portfolio.viewmodel.HomeViewModel
import com.skichrome.portfolio.viewmodel.HomeViewModelFactory

class HomeFragment : Fragment()
{
    // =================================
    //              Fields
    // =================================

    companion object
    {
        private const val RC_SIGN_IN_CODE = 123
    }

    private lateinit var binding: FragmentHomeBinding
    private val viewModel by viewModels<HomeViewModel> { HomeViewModelFactory((requireActivity().application as PortfolioApplication).homeRepository) }
    private var currentUserId: String? = null

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
        checkUserLoggedIn()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN_CODE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                // User successfully logged in
                processNormalFragmentConf()
            }
            else
            {
                // User not logged in, error
                val response = IdpResponse.fromResultIntent(data)
                toast("We can't login now")
                errorLog("AuthUI Error ${response?.error?.errorCode ?: "NO CODE"}", response?.error)
                requireActivity().finish()
            }
        }
    }

    // =================================
    //              Methods
    // =================================

    private fun processNormalFragmentConf()
    {
        configureViewModel()
        configureBinding()
        configureBtn()
    }

    private fun configureViewModel()
    {
        viewModel.message.observe(viewLifecycleOwner, EventObserver { binding.root.snackBar(getString(it)) })
        viewModel.user.observe(viewLifecycleOwner, Observer {
            it?.let { user ->
                loadUserPicture(user.photoReference?.let { photoRef -> Uri.parse(photoRef) })
                currentUserId = user.id
            }
        })
        viewModel.loadFirstUserAvailable()
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
        val opts = HomeFragmentDirections.actionHomeFragmentToProfileFragment(currentUserId)
        findNavController().navigate(opts)
    }

    private fun navigateToThemesFragment()
    {
        findNavController().navigate(R.id.action_homeFragment_to_themesFragment)
    }

    // --- Auth --- //

    private fun checkUserLoggedIn()
    {
        if (FirebaseAuth.getInstance().currentUser == null)
            signInUser()
        else
            processNormalFragmentConf()
    }

    private fun signInUser()
    {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN_CODE
        )
    }
}