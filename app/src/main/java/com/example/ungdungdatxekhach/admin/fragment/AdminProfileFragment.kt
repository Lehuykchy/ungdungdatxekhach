package com.example.ungdungdatxekhach.admin.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.databinding.AdminFragmentProfileBinding

class AdminProfileFragment : Fragment() {
    private lateinit var binding: AdminFragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AdminFragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rltAdminProfileBusManager.setOnClickListener {
            onCLickAdminProfileBusManager()
        }
    }

    private fun onCLickAdminProfileBusManager() {
        val navController = activity?.findNavController(R.id.framelayoutAdmin)
        navController?.navigate(R.id.action_navigation_profile_admin_to_adminProfileVehiceManagerFragment)

    }
}