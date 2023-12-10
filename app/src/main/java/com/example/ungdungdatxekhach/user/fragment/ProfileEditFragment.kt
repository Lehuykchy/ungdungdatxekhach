package com.example.ungdungdatxekhach.user.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.databinding.FragmentProfileEditBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import javax.annotation.Nullable


class ProfileEditFragment : Fragment() {
    private lateinit var binding: FragmentProfileEditBinding
    private lateinit var phone: String
    private val REQUEST_CODE_IMAGE_PICK = 1
    private lateinit var fileName : String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileEditBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView?.visibility = View.GONE

        binding.lnProfileEditBack.setOnClickListener { onClickProfileEditBack() }

        val i = requireActivity().intent
        phone = i.getStringExtra("phone").toString()
        Log.d("checkPhone", "onCreateView: " + phone)

        binding.tvEditcontactaddimg.setOnClickListener {
            val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: $uri")
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }


        binding.btnUpdate.setOnClickListener {  }


        super.onViewCreated(view, savedInstanceState)
    }

    private fun onClickProfileEditBack() {
        val navController = activity?.findNavController(R.id.framelayout)
        navController?.popBackStack()
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView?.visibility = View.VISIBLE
    }

    private fun ischeck(): Boolean {
        if (binding.edtProfileEditName.text.isEmpty()) {
            binding.edtProfileEditName.error = "Hãy nhập tên"
            return false
        }
        if (binding.edtProfileEditEmail.text.isEmpty()) {
            binding.edtProfileEditEmail.error = "Hãy nhập tên"
            return false
        }
        if (binding.edtProfileEditPhone.text.isEmpty()) {
            binding.edtProfileEditPhone.error = "Hãy nhập tên"
            return false
        }
        if (binding.edtProfileEditDate.text.isEmpty()) {
            binding.edtProfileEditDate.error = "Hãy nhập tên"
            return false
        }
        return true
    }

}