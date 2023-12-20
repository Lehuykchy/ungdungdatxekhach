package com.example.ungdungdatxekhach.user.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
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
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.databinding.FragmentProfileEditBinding
import com.example.ungdungdatxekhach.modelshare.City
import com.example.ungdungdatxekhach.modelshare.Location
import com.example.ungdungdatxekhach.user.Utils
import com.example.ungdungdatxekhach.user.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.annotation.Nullable


class ProfileEditFragment : Fragment() {
    private lateinit var binding: FragmentProfileEditBinding
    private lateinit var phone: String
    private val REQUEST_CODE_IMAGE_PICK = 1
    private lateinit var fileName: String
    val db = Firebase.firestore
    val dbUpdate = FirebaseFirestore.getInstance()
    private lateinit var user: User
    private lateinit var location: Location
    private lateinit var cityList: List<City>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileEditBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = User()
        location = Location()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val bottomNavigationView =
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView?.visibility = View.GONE
        binding.lnProfileEditBack.setOnClickListener { onClickProfileEditBack() }

        val i = requireActivity().intent
        phone = i.getStringExtra("phone").toString()
        try {
            db.collection("users").document(phone)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        user = document.toObject<User>()!!
                        location = user.location
                        binding.edtProfileEditName.setText(user.name)
                        binding.edtProfileEditEmail.setText(user.email)
                        binding.tvProfileEditDate.setText(dateFormat.format(user.date))
                        if (!location.city.toString()
                                .isEmpty() || !location.district.toString()
                                .isEmpty() || !location.ward.toString()
                                .isEmpty() || !location.other.toString().isEmpty()
                        ) {
                            binding.tvProfileEditLocation.setText(location.city + "/" + location.district + "/" + location.ward + "/" + location.other)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        binding.edtProfileEditPhone.setText(phone)
        binding.tvProfileEditDate.setOnClickListener { onClickDatePicker() }
        setUpdateUser()
        binding.tvProfileEditLocation.setOnClickListener {
            onClickTvLocation()
        }


    }

    private fun onClickTvLocation() {
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


        val jsonData = Utils.readJsonFromRawResource(requireContext(), R.raw.location)
        cityList = Gson().fromJson(jsonData, object : TypeToken<List<City>>() {}.type)
        val cityNames = cityList.map { it.Name }
        val cityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cityNames)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDialogCity.adapter = cityAdapter

        spinnerDialogCity.onItemSelectedListener =
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
                        requireActivity(),
                        android.R.layout.simple_spinner_item,
                        districtNames
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
                                    requireActivity(),
                                    android.R.layout.simple_spinner_item,
                                    wardNames
                                )
                                wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                spinnerDialogWard.adapter = wardAdapter
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



        ok.setOnClickListener {
            if (edtDialogOther.text.isEmpty()) {
                edtDialogOther.error = "Hãy nhập tên"
            } else {
                if (location != null) {
                    location.city = spinnerDialogCity.selectedItem.toString()
                    location.district = spinnerDialogDistrict.selectedItem.toString()
                    location.ward = spinnerDialogWard.selectedItem.toString()
                    location.other = edtDialogOther.text.toString()
                }
                binding.tvProfileEditLocation.setText(location.city + "/" + location.district + "/" + location.ward + "/" + location.other)
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

    private fun setUpdateUser() {
        binding.btnUpdate.setOnClickListener {
            if (ischeck()) {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val parsedDate: Date = dateFormat.parse(binding.tvProfileEditDate.text.toString())
                val newUser = User(
                    binding.edtProfileEditName.text.toString(),
                    phone,
                    binding.edtProfileEditEmail.text.toString(),
                    parsedDate,
                    location
                )
                updateUser(newUser)
            }
        }
    }

    private fun updateUser(newUser: User) {
        val dataToUpdate = mapOf(
            "name" to newUser.name,
            "phone" to newUser.phone,
            "email" to newUser.email,
            "date" to newUser.date,
            "location" to newUser.location
        )
        dbUpdate.collection("users").document(phone)
            .update(dataToUpdate)
            .addOnSuccessListener {
                Toast.makeText(requireActivity(), "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
            }
    }

    private fun onClickDatePicker() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = requireActivity().let {
            DatePickerDialog(it, { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                binding.tvProfileEditDate.text = selectedDate
            }, year, month, day)
        }
        datePickerDialog!!.show()
    }

    private fun onClickProfileEditBack() {
        val navController = activity?.findNavController(R.id.framelayout)
        navController?.popBackStack()
        val bottomNavigationView =
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
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
        if (binding.tvProfileEditDate.text.isEmpty()) {
            binding.tvProfileEditDate.error = "Hãy nhập tên"
            return false
        }
        return true
    }

}