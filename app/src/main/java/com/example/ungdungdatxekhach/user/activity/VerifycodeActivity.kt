package com.example.ungdungdatxekhach.user.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ungdungdatxekhach.databinding.ActivityVerifycodeBinding

class VerifycodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerifycodeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifycodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}