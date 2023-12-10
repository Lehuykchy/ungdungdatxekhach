package com.example.ungdungdatxekhach.admin.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.databinding.AdminActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: AdminActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.bottomNavigationViewAdmin

        val navController = findNavController(R.id.framelayoutAdmin)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home_admin, R.id.navigation_routes_admin, R.id.navigation_profile_admin
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}