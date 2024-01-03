package com.example.ungdungdatxekhach.admin.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.model.Admin
import com.example.ungdungdatxekhach.databinding.AdminFragmentProfileEditBinding
import com.example.ungdungdatxekhach.modelshare.City
import com.example.ungdungdatxekhach.modelshare.Location
import com.example.ungdungdatxekhach.user.Utils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AdminProfileEditFragment : Fragment() {
    private lateinit var binding: AdminFragmentProfileEditBinding
    private var admin = Admin()
    private val db = Firebase.firestore
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentAdmin = firebaseAuth.currentUser
    private var location = Location()
    private lateinit var cityList: List<City>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AdminFragmentProfileEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDbAdmin()
        binding.btnUpdate.setOnClickListener { 
            if(ischeck()){
                setOnClickUpdate()
            }
        }
        binding.tvAdminProfileEditLocation.setOnClickListener {
            onClickLocation()
            binding.tvAdminProfileEditLocation.text = location.city+"/"+location.district+"/"+location.ward+"/"+location.other
        }
        binding.imgProfileFamilyBack.setOnClickListener {
            onClickBack()
        }
    }

    private fun onClickBack() {
        val navController = activity?.findNavController(R.id.framelayoutAdmin)
        navController?.popBackStack()
        val bottomNavigationView =
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationViewAdmin)
        bottomNavigationView?.visibility = View.VISIBLE
    }

    private fun onClickLocation() {
        val dialog: Dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_location_route);
        dialog.show();

        val ok: Button
        val cancle: Button
        val spinnerDialogCity: Spinner
        val spinnerDialogDistrict: Spinner
        val spinnerDialogWard: Spinner
        val edtDialogOther: EditText

        ok = dialog.findViewById(R.id.btnDialogSave)
        cancle = dialog.findViewById(R.id.btnDialogCancel)
        spinnerDialogCity = dialog.findViewById(R.id.spinnerDialogCity)
        spinnerDialogDistrict = dialog.findViewById(R.id.spinnerDialogDistrict)
        spinnerDialogWard = dialog.findViewById(R.id.spinnerDialogWard)
        edtDialogOther = dialog.findViewById(R.id.edtDialogOther)
        val jsonData = Utils.readJsonFromRawResource(requireActivity(), R.raw.location)
        cityList = Gson().fromJson(jsonData, object : TypeToken<List<City>>() {}.type)
        val cityNames = cityList.map { it.Name }
        val cityAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, cityNames)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDialogCity.adapter = cityAdapter

        spinnerDialogCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long
            ) {
                // Lấy danh sách huyện tương ứng với thành phố được chọn và hiển thị chúng
                val districtNames = cityList[position].districts.map { it.name }
                val districtAdapter = ArrayAdapter(
                    requireActivity(), android.R.layout.simple_spinner_item, districtNames
                )
                districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerDialogDistrict.adapter = districtAdapter

                spinnerDialogDistrict.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parentView: AdapterView<*>?,
                            selectedItemView: View?,
                            position1: Int,
                            id: Long
                        ) {
                            // Lấy danh sách xã tương ứng với huyện được chọn và hiển thị chúng
                            val wardNames =
                                cityList[position].districts[position1].wards.map { it.name }
                            val wardAdapter = ArrayAdapter(
                                requireActivity(), android.R.layout.simple_spinner_item, wardNames
                            )
                            wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinnerDialogWard.adapter = wardAdapter
                        }

                        override fun onNothingSelected(parentView: AdapterView<*>?) {
                        }
                    }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }




        ok.setOnClickListener {
            if (edtDialogOther.text.isEmpty()) {
                edtDialogOther.error = "Hãy nhập tên"
            } else {
                location = Location(
                    spinnerDialogCity.selectedItem.toString(),
                    spinnerDialogDistrict.selectedItem.toString(),
                    spinnerDialogWard.selectedItem.toString(),
                    edtDialogOther.text.toString()
                )
                dialog.dismiss()
            }
        }

        cancle.setOnClickListener {
            dialog.dismiss()
        }

        dialog.getWindow()?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow()?.getAttributes()?.windowAnimations = R.style.DialogAnimation
        dialog.getWindow()?.setGravity(Gravity.BOTTOM);
    }

    private fun setOnClickUpdate() {
        admin = Admin(binding.edtAdminProfileEditEmail.text.toString(),
        binding.edtAdminProfileEditPhone.text.toString(),
        binding.edtAdminProfileEditEmail.text.toString(),
        location,
        "")
        db.collection("admins").document(currentAdmin!!.uid)
            .set(admin)
            .addOnSuccessListener {
                Toast.makeText(requireActivity(), "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
            }


    }

    private fun getDbAdmin() {
        db.collection("admins").document(currentAdmin!!.uid)
            .get()
            .addOnSuccessListener { document ->
                admin = document.toObject(Admin::class.java)!!
                if (admin != null) {
                    binding.edtAdminProfileEditName.setText(admin.name)
                    binding.edtAdminProfileEditPhone.setText(admin.phone)
                    binding.edtAdminProfileEditEmail.setText(admin.email)
                    binding.edtAdminProfileEditDescription.setText(admin?.description)
                    binding.tvAdminProfileEditLocation.setText(
                        admin.location.city + "/" + admin.location.district +
                                "/" + admin.location.ward + "/" + admin.location.other
                    )
                }
            }
    }

    private fun ischeck(): Boolean {
        if (binding.edtAdminProfileEditName.text.isEmpty()) {
            binding.edtAdminProfileEditName.error = "Hãy nhập tên"
            return false
        }
        if (binding.edtAdminProfileEditEmail.text.isEmpty()) {
            binding.edtAdminProfileEditEmail.error = "Hãy nhập tên"
            return false
        }
        if (binding.edtAdminProfileEditPhone.text.isEmpty()) {
            binding.edtAdminProfileEditPhone.error = "Hãy nhập tên"
            return false
        }
        if (binding.tvAdminProfileEditLocation.text.isEmpty()) {
            binding.tvAdminProfileEditLocation.error = "Hãy nhập tên"
            return false
        }
        return true
    }
}