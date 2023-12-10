package com.example.ungdungdatxekhach.user.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.databinding.ActivityAuthInfomationBinding
import com.example.ungdungdatxekhach.user.Utils
import com.example.ungdungdatxekhach.City
import com.example.ungdungdatxekhach.user.viewmodel.SharedViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

class AuthInfomationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthInfomationBinding
    private lateinit var cityList: List<City>
    private val db = Firebase.firestore
    private lateinit var phone : String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthInfomationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        viewModel.dataToPass.observe(this, Observer { data ->
            Log.d("checkphone", "Dữ liệu được truyền từ Fragment: $data")
        })

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
        var i : Intent = Intent(this, PasswordActivity::class.java)
        i.putExtra("name", binding.edtAuthInfoName.text.toString())
        i.putExtra("email", binding.edtAuthInfoEmail.text.toString())
        i.putExtra("phone", binding.tvAuthInfoPhone.text.toString())
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
                    // Lấy danh sách huyện tương ứng với thành phố được chọn và hiển thị chúng
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
                                // Lấy danh sách xã tương ứng với huyện được chọn và hiển thị chúng
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
                                // Không cần xử lý khi không có huyện nào được chọn
                            }
                        }
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {
                    // Không cần xử lý khi không có thành phố nào được chọn
                }
            }

    }


}