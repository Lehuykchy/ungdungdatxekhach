package com.example.ungdungdatxekhach.admin.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.model.Admin
import com.example.ungdungdatxekhach.databinding.AdminFragmentProfileEditBinding
import com.example.ungdungdatxekhach.modelshare.City
import com.example.ungdungdatxekhach.modelshare.Location
import com.example.ungdungdatxekhach.user.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AdminInfomationActivity : AppCompatActivity() {
    private lateinit var binding: AdminFragmentProfileEditBinding
    private lateinit var cityList: List<City>
    private var location = Location()
    private var admin = Admin()
    private val db = Firebase.firestore
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentAdmin = firebaseAuth.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminFragmentProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.cardviewEditcontact.visibility = View.GONE
        binding.tvEditcontactaddimg.visibility = View.GONE

        binding.tvAdminProfileEditLocation.setOnClickListener {
            onClickLocation()
        }
        binding.btnUpdate.setOnClickListener {
            if(ischeck()) {
                setOnClickUpdate()
            }
        }
        binding.imgProfileFamilyBack.setOnClickListener {
            finish()
        }
        
        
    }

    private fun setOnClickUpdate() {
        admin = Admin(binding.edtAdminProfileEditName.text.toString(),
            binding.edtAdminProfileEditPhone.text.toString(),
            binding.edtAdminProfileEditEmail.text.toString(),
            location,
            binding.edtAdminProfileEditDescription.text.toString(),
        "")
        db.collection("admins").document(currentAdmin!!.uid)
            .set(admin)
            .addOnSuccessListener {
                Toast.makeText(this@AdminInfomationActivity, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show()
                startActivity(
                    Intent(
                        this@AdminInfomationActivity,
                        MainActivity::class.java
                    )
                )
            }
            .addOnFailureListener { e ->
            }


    }

    private fun onClickLocation() {
        val dialog: Dialog = Dialog(this@AdminInfomationActivity)
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
        val jsonData = Utils.readJsonFromRawResource(this@AdminInfomationActivity, R.raw.location)
        cityList = Gson().fromJson(jsonData, object : TypeToken<List<City>>() {}.type)
        val cityNames = cityList.map { it.Name }
        val cityAdapter =
            ArrayAdapter(this@AdminInfomationActivity, android.R.layout.simple_spinner_item, cityNames)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDialogCity.adapter = cityAdapter

        spinnerDialogCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long
            ) {
                // Lấy danh sách huyện tương ứng với thành phố được chọn và hiển thị chúng
                val districtNames = cityList[position].districts.map { it.name }
                val districtAdapter = ArrayAdapter(
                    this@AdminInfomationActivity, android.R.layout.simple_spinner_item, districtNames
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
                                this@AdminInfomationActivity, android.R.layout.simple_spinner_item, wardNames
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
                binding.tvAdminProfileEditLocation.text = location.city+"/"+location.district+"/"+location.ward+"/"+location.other
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