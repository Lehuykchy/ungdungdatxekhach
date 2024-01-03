package com.example.ungdungdatxekhach.admin.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.model.Admin
import com.example.ungdungdatxekhach.databinding.AdminFragmentProfileBinding
import com.example.ungdungdatxekhach.modelshare.activity.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminProfileFragment : Fragment() {
    private lateinit var binding: AdminFragmentProfileBinding
    private val db = Firebase.firestore
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentAdmin = firebaseAuth.currentUser
    private var admin= Admin()
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
        getAdmin()

        binding.rltAdminProfile.setOnClickListener {
            onClickProfileEdit()
        }
        binding.rltAdminProfileBusManager.setOnClickListener {
            onCLickAdminProfileBusManager()
        }
        binding.rltAdminProfileLogout.setOnClickListener {
            onClickLogout()
        }
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
            com.google.firebase.Firebase.auth.signOut()
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

    private fun onClickProfileEdit() {
        val bottomNavigationView =
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationViewAdmin)
        bottomNavigationView?.visibility = View.GONE
        val navController = activity?.findNavController(R.id.framelayoutAdmin)
        navController?.navigate(R.id.action_navigation_profile_admin_to_adminProfileEditFragment)
    }

    private fun getAdmin() {
        db.collection("admins").document(currentAdmin!!.uid)
            .get()
            .addOnSuccessListener { document ->
                admin = document.toObject(Admin::class.java)!!
                binding.tvAdminProfileName.text = admin.name
                binding.tvAdminProfilePhone.text = admin.phone

            }
    }

    private fun onCLickAdminProfileBusManager() {
        val bottomNavigationView =
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationViewAdmin)
        bottomNavigationView?.visibility = View.GONE
        val navController = activity?.findNavController(R.id.framelayoutAdmin)
        navController?.navigate(R.id.action_navigation_profile_admin_to_adminProfileVehiceManagerFragment)

    }
}