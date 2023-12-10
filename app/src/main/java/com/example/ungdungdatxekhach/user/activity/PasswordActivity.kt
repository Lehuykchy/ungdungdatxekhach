package com.example.ungdungdatxekhach.user.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.ungdungdatxekhach.databinding.ActicityPasswordBinding
import com.example.ungdungdatxekhach.user.model.User
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase

class PasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActicityPasswordBinding
    private val db = Firebase.firestore
    private var checkPassword: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActicityPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdtPhonenumber()
        binding.btnLoginPassword.isEnabled = false

        binding.imgBackLogin.setOnClickListener {
            finish()
        }


        val receivedIntent = intent

        // Kiểm tra giá trị có null không
        val name = receivedIntent.getStringExtra("name") ?: ""
        val email = receivedIntent.getStringExtra("email") ?: ""
        val phone = receivedIntent.getStringExtra("phone") ?: ""

        if (name.isNotEmpty() && email.isNotEmpty()) {


            binding.btnLoginPassword.setOnClickListener {
                val password = binding.edtPassword.text.toString()
                var user = User(name, phone, email, password)

                db.collection("users").document(phone)
                    .set(user)
                    .addOnSuccessListener {
                        val i : Intent = Intent(this, MainActivity::class.java)
                        i.putExtra("phone", phone)
                        startActivity(i)
                    }
                    .addOnFailureListener { e -> Log.w("firestoredb", "Error writing document", e) }
            }
        } else {
            binding.btnLoginPassword.setOnClickListener {
                val docRef = db.collection("users").document(phone)
                docRef.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot != null) {
                        val user = documentSnapshot.toObject(User::class.java)
                        if (user != null) {
                            if (user.checkPassword(binding.edtPassword.text.toString())) {
                                val i : Intent = Intent(this, MainActivity::class.java)
                                i.putExtra("phone", phone)
                                startActivity(i)
                            }else{
                                binding.edtPassword.error = "Nhập sai mật khẩu"
                            }
                        } else {
                            Log.d("password123", "faild")
                        }
                    } else {
                        Log.d("password123", "faildas")
                    }
                }
            }
        }
    }
    private fun setEdtPhonenumber() {
        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length!! > 0) {
                    binding.btnLoginPassword.setBackgroundColor(Color.parseColor("#00cba9"))
                    binding.btnLoginPassword.isEnabled = true
                    binding.btnLoginPassword.setTextColor(Color.parseColor("#FFFFFF"))
                } else {
                    binding.btnLoginPassword.setBackgroundColor(Color.parseColor("#858484"))
                    binding.btnLoginPassword.isEnabled = false
                    binding.btnLoginPassword.setTextColor(Color.parseColor("#E6E3E3"))
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }
}