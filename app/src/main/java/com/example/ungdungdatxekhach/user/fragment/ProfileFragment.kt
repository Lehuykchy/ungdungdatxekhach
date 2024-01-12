package com.example.ungdungdatxekhach.user.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.Constants
import com.example.ungdungdatxekhach.databinding.FragmentProfileBinding
import com.example.ungdungdatxekhach.modelshare.activity.LoginActivity
import com.example.ungdungdatxekhach.user.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    val db = com.google.firebase.ktx.Firebase.firestore
    private lateinit var user: User
    private lateinit var phone: String
    private lateinit var storageReference: StorageReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val bottomNavigationView =
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView?.visibility = View.VISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val i = requireActivity().intent
        phone = i.getStringExtra("phone").toString()

        binding.rltProfile.setOnClickListener { onClickRltProfile() }
        binding.rltProfileLogout.setOnClickListener { onClickLogout() }
        binding.rltProfileChangePassword.setOnClickListener {
            onClickChangePassword()
        }
        user = User()
        db.collection("users").document(phone).get().addOnSuccessListener { document ->
            if (document != null) {
                user = document.toObject<User>()!!
                binding.tvNameProfile.text = user.name
                binding.tvPhoneProfile.text = "0"+user.phone
                val storagePath = "images/" + user.imageId //
                val storage = FirebaseStorage.getInstance()
                val storageRef = storage.reference.child(storagePath)
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    Glide.with(this)
                        .load(downloadUrl)
                        .into(binding.imgProfile)
                }.addOnFailureListener { exception ->
                    Log.e("Firebase Storage", "Error getting download URL: ${exception.message}")
                }
            }
        }.addOnFailureListener { exception ->
        }

    }

    private fun onClickChangePassword() {
        val dialog: Dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_change_password);
        dialog.show();

        val ok: Button
        val cancle: Button
        val edtDialogChangePassword: EditText
        val edtDialogChangeNewPassword: EditText
        val edtDialogChangeNewPasswordReturn: EditText

        ok = dialog.findViewById(R.id.btnDialogChangePasswordConfirm)
        cancle = dialog.findViewById(R.id.btnDialogChangePasswordCancel)
        edtDialogChangePassword = dialog.findViewById(R.id.edtDialogChangePassword)
        edtDialogChangeNewPassword = dialog.findViewById(R.id.edtDialogChangeNewPassword)
        edtDialogChangeNewPasswordReturn =
            dialog.findViewById(R.id.edtDialogChangeNewPasswordReturn)

        ok.setOnClickListener {
            if (user.password != edtDialogChangePassword.text.toString()) {
                edtDialogChangePassword.error = "Nhập sai mật khẩu hiện tại"
            }else if(!edtDialogChangeNewPassword.text.toString().equals(edtDialogChangeNewPasswordReturn.text.toString()) &&
                (edtDialogChangeNewPassword.text.isNotEmpty() && edtDialogChangeNewPasswordReturn.text.isNotEmpty())){
                edtDialogChangeNewPasswordReturn.error = "Nhập khẩu mới tạo không giống nhau"
            }else{
                user.password = edtDialogChangeNewPassword.text.toString()
                val dataToUpdate = mapOf(
                    "hashedPassword" to user.hashPasswordChange(user.password!!) ,
                    "password" to user.password,
                )
                db.collection("users").document(phone)
                    .update(dataToUpdate)
                    .addOnSuccessListener {
                        Toast.makeText(requireActivity(), "Cập nhật mật khẩu thành công!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                    }
                dialog.dismiss()
            }

        }

        cancle.setOnClickListener {
            dialog.dismiss()
        }

        dialog.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow()?.getAttributes()?.windowAnimations = R.style.DialogAnimation
        dialog.getWindow()?.setGravity(Gravity.BOTTOM);

    }

    private fun onClickLogout() {
        val dialog: Dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_bottom_sheet);
        dialog.show();

        val ok: TextView
        val cancle: TextView

        ok = dialog.findViewById(R.id.tvBottomSheetOk)
        cancle = dialog.findViewById(R.id.tvBottomSheetCancle)

        ok.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            dialog.dismiss()
        }

        cancle.setOnClickListener {
            dialog.dismiss()
        }

        dialog.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow()?.getAttributes()?.windowAnimations = R.style.DialogAnimation
        dialog.getWindow()?.setGravity(Gravity.BOTTOM);
    }

    private fun onClickRltProfile() {
        val navController = activity?.findNavController(R.id.framelayout)
        navController?.navigate(R.id.action_navigation_profile_to_profileEditFragment)
    }
}