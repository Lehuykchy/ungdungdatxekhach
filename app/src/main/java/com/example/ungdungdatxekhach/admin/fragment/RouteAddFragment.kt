package com.example.ungdungdatxekhach.admin.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungdatxekhach.modelshare.Location
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.adapter.ItemLocationRouteAdapter
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.databinding.AdminFragmentRoutesAddBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class RouteAddFragment : Fragment() {
    private lateinit var binding: AdminFragmentRoutesAddBinding
    private val db = Firebase.firestore
    private lateinit var id : String
    private lateinit var adapterLocation: ItemLocationRouteAdapter
    private lateinit var listItemLocation : ArrayList<Location>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AdminFragmentRoutesAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNavigationView =
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationViewAdmin)
        bottomNavigationView?.visibility = View.GONE

        listItemLocation = ArrayList()
        binding.rcvRouteAdminAdd.layoutManager = LinearLayoutManager(requireActivity())
        adapterLocation = ItemLocationRouteAdapter(listItemLocation, requireActivity(), object : ItemLocationRouteAdapter.ClickContactListener{
            override fun clickLn(id: Int) {
                Log.d("checklist", "clickLn: " + listItemLocation.size.toString())
            }

        })
        binding.rcvRouteAdminAdd.adapter = adapterLocation
        binding.imgRoutesAddBack.setOnClickListener { onClickImgRouteAddBack() }
        binding.btnUpdate.setOnClickListener {
            if (ischeck()) {
                onCLickSave()
            }
        }
        binding.lnAddRoute.setOnClickListener {
            setOnClickAddLocation()
        }
    }

    private fun setOnClickSelectTime() {
        val materialTimePicker: MaterialTimePicker = MaterialTimePicker.Builder()
            .setTitleText("SELECT YOUR TIMING")
            .setHour(12)
            .setMinute(10)
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .build()

        materialTimePicker.show(requireActivity().supportFragmentManager, "RouteAddFragment")
        materialTimePicker.addOnPositiveButtonClickListener {
            val pickedHour: Int = materialTimePicker.hour
            val pickedMinute: Int = materialTimePicker.minute
            Log.d("checktime", "setOnClickSelectTime: " + pickedHour.toString() + ":" + pickedMinute.toString())
        }
    }

    private fun setOnClickAddLocation() {
        var location = Location("", "", "", "")
        adapterLocation.addLocationRoute(location)
    }

    private fun onCLickSave() {
        val user = Firebase.auth.currentUser
        user?.let {
            id = it.uid.toString()
        }
        val route: Route = Route(
            id,
            binding.edtRoutesAddDepartureLocation.text.toString(),
            binding.edtRoutesAddDestination.text.toString(),
            binding.edtRoutesAddTotalTime.text.toString().toInt(),
            binding.edtRoutesAddDistance.text.toString(),
            binding.edtRoutesAddPrice.text.toString(),
            listItemLocation,
        )

        db.collection("routes").document()
            .set(route)
            .addOnSuccessListener {
                onClickImgRouteAddBack()
            }
            .addOnFailureListener {}
    }

    private fun onClickImgRouteAddBack() {
        val navController = activity?.findNavController(R.id.framelayoutAdmin)
        navController?.popBackStack()
        val bottomNavigationView =
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationViewAdmin)
        bottomNavigationView?.visibility = View.VISIBLE
    }

    private fun ischeck(): Boolean {
        if (binding.edtRoutesAddDepartureLocation.text.isEmpty()) {
            binding.edtRoutesAddDepartureLocation.error = "Hãy nhập tên"
            return false
        }
        if (binding.edtRoutesAddDestination.text.isEmpty()) {
            binding.edtRoutesAddDestination.error = "Hãy nhập tên"
            return false
        }
        if (binding.edtRoutesAddTotalTime.text.isEmpty()) {
            binding.edtRoutesAddTotalTime.error = "Hãy nhập tên"
            return false
        }
        if (binding.edtRoutesAddDistance.text.isEmpty()) {
            binding.edtRoutesAddDistance.error = "Hãy nhập tên"
            return false
        }

        return true
    }
}