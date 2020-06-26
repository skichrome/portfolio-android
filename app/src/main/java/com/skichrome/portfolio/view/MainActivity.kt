package com.skichrome.portfolio.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.skichrome.portfolio.R
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : AppCompatActivity()
{
    // =================================
    //        Superclass Methods
    // =================================

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navController = getNavController()
        configureToolbar(navController)
    }

    // =================================
    //              Methods
    // =================================

    private fun getNavController(): NavController = findNavController(R.id.activity_nav_host_fragment)

    private fun configureToolbar(navController: NavController) = toolbar?.apply {
        setupWithNavController(navController)
    }
}
