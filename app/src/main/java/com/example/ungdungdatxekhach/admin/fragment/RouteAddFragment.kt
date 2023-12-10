package com.example.ungdungdatxekhach.admin.fragment

import android.app.Dialog
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
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungdatxekhach.Location
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.Time
import com.example.ungdungdatxekhach.admin.adapter.ItemLocationRouteAdapter
import com.example.ungdungdatxekhach.admin.adapter.ItemTimeAdapter
import com.example.ungdungdatxekhach.admin.model.Route
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
    private lateinit var listItemTime : ArrayList<Time>
    private lateinit var adapterTimes: ItemTimeAdapter
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


        listItemTime = ArrayList()
        binding.rcvRouteAdminTimes.layoutManager = GridLayoutManager(requireActivity(), 3)
        adapterTimes = ItemTimeAdapter(listItemTime, requireActivity(), object : ItemTimeAdapter.IClickListener{
            override fun clickDelete(id: Int) {

            }
        })
        binding.rcvRouteAdminTimes.adapter = adapterTimes

        binding.imgRoutesAddBack.setOnClickListener { onClickImgRouteAddBack() }
        binding.btnUpdate.setOnClickListener {
            if (ischeck()) {
                onCLickSave()
            }
        }
        binding.lnAddRoute.setOnClickListener {
            setOnClickAddLocation()
        }
        binding.lnRouteAdminSelectTime.setOnClickListener {
            setOnClickSelectTime()
        }
    }

    private fun setOnClickSelectTime() {
        val materialTimePicker: MaterialTimePicker = MaterialTimePicker.Builder()
            // set the title for the alert dialog
            .setTitleText("SELECT YOUR TIMING")
            // set the default hour for the
            // dialog when the dialog opens
            .setHour(12)
            // set the default minute for the
            // dialog when the dialog opens
            .setMinute(10)
            // set the time format
            // according to the region
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .build()

        materialTimePicker.show(requireActivity().supportFragmentManager, "RouteAddFragment")

        // on clicking the positive button of the time picker
        // dialog update the TextView accordingly
        materialTimePicker.addOnPositiveButtonClickListener {

            val pickedHour: Int = materialTimePicker.hour
            val pickedMinute: Int = materialTimePicker.minute
            val time = Time(pickedHour, pickedMinute)
            adapterTimes.addTime(time)
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
            binding.edtRoutesAddTotalTime.text.toString(),
            binding.edtRoutesAddDistance.text.toString(),
            binding.edtRoutesAddPrice.text.toString(),
            listItemLocation,
            listItemTime,
            ""
        )

        db.collection("admins").document()
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