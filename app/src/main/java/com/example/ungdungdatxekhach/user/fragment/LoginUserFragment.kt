package com.example.ungdungdatxekhach.user.fragment

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ungdungdatxekhach.databinding.FragmentLoginUserBinding
import com.example.ungdungdatxekhach.user.activity.AuthInfomationActivity
import com.example.ungdungdatxekhach.user.activity.PasswordActivity
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.firestore
import java.util.concurrent.TimeUnit

class LoginUserFragment : Fragment() {
    private lateinit var binding: FragmentLoginUserBinding
    private var mAuth: FirebaseAuth? = null
    private var verifiId: String? = null
    private var phone: String = ""
    val db = Firebase.firestore


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginUserBinding.inflate(inflater, container, false)

        binding.btnLogin.isEnabled = false
        binding.btnLoginVertifyOTP.isEnabled = false

        mAuth = FirebaseAuth.getInstance()


        setEdtPhonenumber()
        setBtnLogin()
        setImgBackLogin()
        setEdtOtp()
        setBtnLoginVertifyOTP()

        return binding.root
    }

    private fun setBtnLoginVertifyOTP() {
        binding.btnLoginVertifyOTP.setOnClickListener {
            if (TextUtils.isEmpty(phone) ){
                Toast.makeText(activity, "Please enter OTP", Toast.LENGTH_SHORT).show();
            } else {
                verifyCode(binding.edtOTP.getText().toString());
                Log.d("otp", "setBtnLoginVertifyOTP: " + verifiId.toString())
            }
        }


    }

    private fun verifyCode(toString: String) {
        val credential = PhoneAuthProvider.getCredential(verifiId.toString(), toString)
        signInWithCredential(credential)


    }


    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val i: Intent = Intent(requireContext(), AuthInfomationActivity::class.java)
                    i.putExtra("phone", phone)
                    requireContext().startActivity(i)
                } else {
                    Toast.makeText(requireActivity(), "Mã otp không hợp lệ vui lòng nhập lại!",Toast.LENGTH_SHORT).show()
                    binding.edtOTP.setText("")
                    Log.d("otp", task.exception!!.message.toString())
                }
            }
    }


    private fun setEdtOtp() {
        binding.edtOTP.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length!! > 0) {
                    binding.btnLoginVertifyOTP.setBackgroundColor(Color.parseColor("#00cba9"))
                    binding.btnLoginVertifyOTP.isEnabled = true
                    binding.btnLoginVertifyOTP.setTextColor(Color.parseColor("#FFFFFF"))
                } else {
                    binding.btnLoginVertifyOTP.setBackgroundColor(Color.parseColor("#858484"))
                    binding.btnLoginVertifyOTP.isEnabled = false
                    binding.btnLoginVertifyOTP.setTextColor(Color.parseColor("#E6E3E3"))
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun setImgBackLogin() {
        binding.imgBackLogin.setOnClickListener {
            binding.lnLogin.visibility = View.VISIBLE
            binding.lnLoginVertifyOTP.visibility = View.GONE
        }
    }

    private fun setBtnLogin() {
        binding.btnLogin.setOnClickListener {
            if (phone?.startsWith("0") == true) {
                phone = phone!!.substring(1)
            }
            Log.d("checkphone", "setBtnLogin: "+phone)
            val docRef = db.collection("users").document(phone!!)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val i: Intent = Intent(requireContext(), PasswordActivity::class.java)
                        i.putExtra("phone", phone!!)
                        requireContext().startActivity(i)
                    } else {
                        binding.lnLogin.visibility = View.GONE
                        binding.lnLoginVertifyOTP.visibility = View.VISIBLE
                        sendVerificationCode("+84" + phone!!);
                        Log.d("otp", "+84" + phone!!)
//                        val i: Intent = Intent(requireContext(), AuthInfomationActivity::class.java)
//                        i.putExtra("phone", binding.edtPhone.text.toString())
//                        requireContext().startActivity(i)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }

        }
    }

    private fun sendVerificationCode(phonenumber: String) {
        val options = PhoneAuthOptions.newBuilder(mAuth!!)
            .setPhoneNumber(phonenumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    var code = credential.getSmsCode()
                    Log.d("otpcode", "onVerificationCompleted: " + code.toString())
                    if (code != null) {
                        binding.edtOTP.setText(code);
                        verifyCode(binding.edtOTP.toString())
                    } else {
                        Log.d("otp", "onVerificationCompleted: code null")
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.d("otp", "onVerificationFailed", e)
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken,
                ) {
                    verifiId = verificationId
                }

            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    private fun setEdtPhonenumber() {
        binding.edtPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length!! > 0) {
                    binding.btnLogin.setBackgroundColor(Color.parseColor("#00cba9"))
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.setTextColor(Color.parseColor("#FFFFFF"))
                    phone = binding.edtPhone.text.toString()
                    Log.d("checkphone", "onTextChanged: "+phone)
                } else {
                    binding.btnLogin.setBackgroundColor(Color.parseColor("#858484"))
                    binding.btnLogin.isEnabled = false
                    binding.btnLogin.setTextColor(Color.parseColor("#E6E3E3"))
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }
}