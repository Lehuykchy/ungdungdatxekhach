package com.example.ungdungdatxekhach.user.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.databinding.ActivityMainBinding
import com.example.ungdungdatxekhach.databinding.AdminActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.bottomNavigationView
        val navController = findNavController(R.id.framelayout)
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, _, _ -> }

    }
}