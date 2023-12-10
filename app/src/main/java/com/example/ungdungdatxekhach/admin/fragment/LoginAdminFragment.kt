package com.example.ungdungdatxekhach.admin.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ungdungdatxekhach.admin.activity.MainActivity
import com.example.ungdungdatxekhach.databinding.FragmentLoginAdminBinding
import com.example.ungdungdatxekhach.databinding.FragmentLoginUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginAdminFragment : Fragment() {
    private lateinit var binding: FragmentLoginAdminBinding
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            auth = Firebase.auth
            var email = binding.edtAccount.text.toString()
            var password = binding.edtPassword.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
                    } else {
                        Log.w("login", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(requireContext(), "Không đăng nhập thành công", Toast.LENGTH_SHORT).show()
                    }
                }

        }

    }

}