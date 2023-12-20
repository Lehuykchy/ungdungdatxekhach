package com.example.ungdungdatxekhach.admin.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ungdungdatxekhach.databinding.AdminFragmentHomeBinding

class AdminHomeFragment : Fragment(){
    private lateinit var binding: AdminFragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AdminFragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
}