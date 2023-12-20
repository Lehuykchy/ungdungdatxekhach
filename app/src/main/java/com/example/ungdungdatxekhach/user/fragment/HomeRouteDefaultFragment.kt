package com.example.ungdungdatxekhach.user.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.adapter.ItemRouteScheduleAdapter
import com.example.ungdungdatxekhach.admin.model.Vehicle
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.databinding.FragmentHomeRouteDefaultBinding
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeRouteDefaultFragment : Fragment() {
    private lateinit var binding: FragmentHomeRouteDefaultBinding
    private lateinit var scheduletList : ArrayList<Schedule>
    private lateinit var vehicleList : ArrayList<Vehicle>
    private lateinit var adapter: ItemRouteScheduleAdapter
    private lateinit var route : Route
    private lateinit var phone : String
    val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeRouteDefaultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDate()

        val receivedBundle = arguments
        if (receivedBundle != null && receivedBundle.containsKey("route")) {
            route = receivedBundle.getSerializable("route") as Route
            phone = receivedBundle.getSerializable("phone") as String
        }

        binding.imgRouteDefaultBackUser.setOnClickListener {
            onClickHomeRouteDefaultBack()
        }

        binding.tvRouteDefaultClickDateUser.setOnClickListener {
            showDatePickerDialog()
        }

        scheduletList = ArrayList()
        getTicketList()
        vehicleList = ArrayList()
        getVehicleList()
        binding.rcvScheduleUser.layoutManager = LinearLayoutManager(requireActivity())
        adapter = ItemRouteScheduleAdapter(
            scheduletList,
            vehicleList,
            requireActivity(),
            object : ItemRouteScheduleAdapter.IClickListener {
                override fun clickDelete(id: Int) {
                }

                override fun onClick(position: Int) {
                    var schedule = scheduletList.get(position)
                    val bundle = bundleOf("route" to route, "schedule" to schedule, "phone" to phone)
                    val navController = activity?.findNavController(R.id.framelayout)
                    navController?.navigate(R.id.action_homeRouteDefaultFragment_to_routeDefaultBuyTicketStep1, bundle)
                }


            },
            route
        )
        binding.rcvScheduleUser.adapter = adapter


    }

    private fun getVehicleList() {
        db.collection("admins").document(route.idAdmin).collection("vehicles")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val vehicle = document.toObject<Vehicle>()
                    if (vehicle != null) {
                        vehicle.id = document.id
                        vehicleList.add(vehicle)
                    }
                }
            }
            .addOnFailureListener { exception ->
            }
    }

    private fun getTicketList() {
        scheduletList.clear()
        val dateString = binding.tvRouteDefaultDateUser.text.toString()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        try {
            val parsedDate: Date = dateFormat.parse(dateString)

            db.collection("admins").document(route.idAdmin).collection("tickets")
//                .whereEqualTo("date", parsedDate)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val ticket = document.toObject<Schedule>()
                        if (ticket != null) {
                            adapter.addTicket(ticket)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                }
        } catch (e: ParseException) {
            e.printStackTrace()
        }


    }


    private fun getDate() {
        val date = Date()
        val formatDate = SimpleDateFormat("dd/MM/yyyy")
        val dateEd = formatDate.format(date)
        binding.tvRouteDefaultDateUser.text = dateEd
    }
    private fun showDatePickerDialog() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = requireActivity().let {
            DatePickerDialog(it, { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                binding.tvRouteDefaultDateUser.text = selectedDate
                getTicketList()
            }, year, month, day)
        }
        datePickerDialog.datePicker.minDate = currentDate.timeInMillis
        datePickerDialog!!.show()
    }
    private fun onClickHomeRouteDefaultBack() {
        val navController = activity?.findNavController(R.id.framelayout)
        navController?.popBackStack()
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView?.visibility = View.VISIBLE
    }

}