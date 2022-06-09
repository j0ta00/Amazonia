package com.example.juanjomz.amazonia.ui.view.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavArgument
import androidx.navigation.findNavController


import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.juanjomz.amazonia.R
import com.example.juanjomz.amazonia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding!!.root
        setContentView(view)



        val navController=findNavController(R.id.nav_host_fragment)
        binding!!.navigationMenu.setupWithNavController(navController = navController)
        val appBarConfiguration= AppBarConfiguration(setOf(R.id.galleryFragment,
            R.id.plantListFragment,
            R.id.plantIdentification,
            R.id.userDetailsFragment))
        setupActionBarWithNavController(navController,appBarConfiguration)
    }
}