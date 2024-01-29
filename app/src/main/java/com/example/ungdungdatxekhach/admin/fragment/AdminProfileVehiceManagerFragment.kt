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
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.adapter.ItemVehiceManagerAdapter
import com.example.ungdungdatxekhach.admin.model.Vehicle
import com.example.ungdungdatxekhach.databinding.AdminFragmentProfileVehiceManagerBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class AdminProfileVehiceManagerFragment : Fragment() {
    private lateinit var binding: AdminFragmentProfileVehiceManagerBinding
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    val id = currentUser!!.uid
    val db = Firebase.firestore
    private lateinit var adapter: ItemVehiceManagerAdapter
    private lateinit var listVehice: ArrayList<Vehicle>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AdminFragmentProfileVehiceManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationViewAdmin)
        bottomNavigationView?.visibility = View.GONE

        listVehice = ArrayList()
        getListData()
        binding.rcvProfileBusManager.layoutManager = LinearLayoutManager(requireActivity())
        adapter = ItemVehiceManagerAdapter(listVehice, requireActivity(), object: ItemVehiceManagerAdapter.IClickListener{
            override fun clickDelete(id: Int) {

            }

            override fun clickNext(id: Int) {

            }

        })
        binding.rcvProfileBusManager.adapter = adapter

        binding.imgProfileBusManagerAdd.setOnClickListener {
            onClickProfileBusManagerAdd()
        }
        binding.imgProfileBusManagerBack.setOnClickListener {
            onClickProfileBusManagerBack()
        }

    }

    private fun getListData() {
        db.collection("admins").document(id).collection("vehicles")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val vehicle = document.toObject<Vehicle>()
                    vehicle.id = document.id
                    if(vehicle!= null) {
                        adapter.addVehice(vehicle)
                    }
                }
            }
            .addOnFailureListener { exception ->
            }
    }

    private fun onClickProfileBusManagerBack() {
        val navController = activity?.findNavController(R.id.framelayoutAdmin)
        navController?.popBackStack()
    }

    private fun onClickProfileBusManagerAdd() {
        val dialog : Dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.admin_dialog_add_vehice);
        dialog.show();

        val tvCancel = dialog.findViewById<TextView>(R.id.tvAddVihiceCancle)
        val tvSave = dialog.findViewById<TextView>(R.id.tvAddVihiceSave)
        val edtLicensePlate = dialog.findViewById<EditText>(R.id.edtAddVihiceLicensePlate)
        val spinnerType = dialog.findViewById<Spinner>(R.id.spinnerAddVihiceType)
        val spinnerSeat = dialog.findViewById<Spinner>(R.id.spinnerAddVihiceSeat)

        spinnerType.prompt= "Chọn kiểu xe"
        spinnerSeat.prompt= "Chọn số chỗ ngồi"
        ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.VehiceType,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerType.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.VehiceSeat,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSeat.adapter = adapter
        }


        tvSave.setOnClickListener {
            if(edtLicensePlate.text.isEmpty()){
                edtLicensePlate.error = "Nhập biển số xe"
            }else{
                var vehicle = Vehicle(id, spinnerType.selectedItem.toString()
                    , edtLicensePlate.text.toString(), spinnerSeat.selectedItem.toString().toInt())
                adapter.addVehice(vehicle)

                db.collection("admins").document(id).collection("vehicles")
                    .add(vehicle)
                    .addOnSuccessListener { documentReference ->
                       Toast.makeText(requireActivity(), "Thêm phương tiện thành công", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                    }


                dialog.dismiss()
            }

        }

        tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow()?.getAttributes()?.windowAnimations = R.style.DialogAnimation
        dialog.getWindow()?.setGravity(Gravity.BOTTOM);
    }
}