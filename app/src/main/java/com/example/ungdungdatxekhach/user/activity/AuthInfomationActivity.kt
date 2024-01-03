package com.example.ungdungdatxekhach.user.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.databinding.ActivityAuthInfomationBinding
import com.example.ungdungdatxekhach.user.Utils
import com.example.ungdungdatxekhach.modelshare.City
import com.example.ungdungdatxekhach.modelshare.Location
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AuthInfomationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthInfomationBinding
    private lateinit var cityList: List<City>
    private val db = Firebase.firestore
    private lateinit var phone: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthInfomationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phone = intent.getStringExtra("phone").toString()
        binding.tvAuthInfoPhone.text = phone
        binding.btnContinue.setOnClickListener {
            if (ischeck()) {
                var i: Intent = Intent(this, MainActivity::class.java)
                startActivity(i)
            }
        }
        binding.tvAuthInfoDate.setOnClickListener { showDatePickerDialog() }
        setClickSpinner()
        binding.btnContinue.setOnClickListener { onClickContinue() }
    }

    private fun onClickContinue() {
        var i: Intent = Intent(this, PasswordActivity::class.java)
        i.putExtra("name", binding.edtAuthInfoName.text.toString())
        i.putExtra("email", binding.edtAuthInfoEmail.text.toString())
        i.putExtra("phone", binding.tvAuthInfoPhone.text.toString())
        i.putExtra("city", binding.spinnerAuthInfoCity.selectedItem.toString())
        i.putExtra("district", binding.spinnerAuthInfoDistrict.selectedItem.toString())
        i.putExtra("ward", binding.spinnerAuthInfoWard.selectedItem.toString())
        i.putExtra("date", binding.tvAuthInfoDate.text.toString())
        startActivity(i)
    }

    private fun ischeck(): Boolean {
        if (binding.edtAuthInfoName.text.isEmpty()) {
            binding.edtAuthInfoName.error = "Hãy nhập tên"
            return false
        }
        if (binding.edtAuthInfoEmail.text.isEmpty()) {
            binding.edtAuthInfoEmail.error = "Hãy nhập tên"
            return false
        }
        if (binding.tvAuthInfoDate.text.isEmpty()) {
            binding.tvAuthInfoDate.error = "Hãy nhập tên"
            return false
        }
        return true
    }

    private fun showDatePickerDialog() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = this.let {
            DatePickerDialog(it, { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                binding.tvAuthInfoDate.text = selectedDate
            }, year, month, day)
        }
        datePickerDialog!!.show()
    }


    private fun setClickSpinner() {
        val jsonData = Utils.readJsonFromRawResource(this, R.raw.location)
        cityList = Gson().fromJson(jsonData, object : TypeToken<List<City>>() {}.type)
        val cityNames = cityList.map { it.Name }
        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cityNames)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAuthInfoCity.adapter = cityAdapter

        binding.spinnerAuthInfoCity.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    val districtNames = cityList[position].districts.map { it.name }
                    val districtAdapter = ArrayAdapter(
                        this@AuthInfomationActivity,
                        android.R.layout.simple_spinner_item,
                        districtNames
                    )
                    districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerAuthInfoDistrict.adapter = districtAdapter
                    binding.spinnerAuthInfoDistrict.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parentView: AdapterView<*>?,
                                selectedItemView: View?,
                                position1: Int,
                                id: Long
                            ) {
                                val wardNames =
                                    cityList[position].districts[position1].wards.map { it.name }
                                val wardAdapter = ArrayAdapter(
                                    this@AuthInfomationActivity,
                                    android.R.layout.simple_spinner_item,
                                    wardNames
                                )
                                wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                binding.spinnerAuthInfoWard.adapter = wardAdapter
                            }
                            override fun onNothingSelected(parentView: AdapterView<*>?) {
                            }
                        }
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {
                }
            }

    }


}