package com.example.ungdungdatxekhach.admin.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.adapter.ItemRouteScheduleAdapter
import com.example.ungdungdatxekhach.admin.model.Vehicle
import com.example.ungdungdatxekhach.databinding.AdminFragmentRoutesDefaultBinding
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.example.ungdungdatxekhach.modelshare.TimeRoute
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AdminRouteDefaultFragment : Fragment() {
    private lateinit var binding: AdminFragmentRoutesDefaultBinding
    private lateinit var vehicleList: ArrayList<Vehicle>
    private lateinit var scheduleList: ArrayList<Schedule>
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    val id = currentUser!!.uid
    val db = Firebase.firestore
    private lateinit var adapter: ItemRouteScheduleAdapter
    private lateinit var route: Route
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AdminFragmentRoutesDefaultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNavigationView =
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationViewAdmin)
        bottomNavigationView?.visibility = View.GONE
        getDate()

        val receivedBundle = arguments
        if (receivedBundle != null && receivedBundle.containsKey("route")) {
            route = receivedBundle.getSerializable("route") as Route
        }
        vehicleList = ArrayList()
        getVehicleList()
        scheduleList = ArrayList()
        getscheduleList()



        binding.rcvSchedule.layoutManager = LinearLayoutManager(requireActivity())
        adapter = ItemRouteScheduleAdapter(
            scheduleList,
            requireActivity(),
            object : ItemRouteScheduleAdapter.IClickListener {
                override fun clickDelete(id: Int) {
                }

                override fun onClick(position: Int, route: Route) {
                    var schedule = scheduleList.get(position)
                    val bundle = bundleOf("schedule" to schedule)
                    val navController = activity?.findNavController(R.id.framelayoutAdmin)
                    navController?.navigate(R.id.action_layoutRouteDefaultFragment_to_adminRouteDefaultTicketFragment, bundle)

                }

            },
        )
        binding.rcvSchedule.adapter = adapter

        binding.imgAddSchedule.setOnClickListener {
            onClickAddschedule()
        }
        binding.tvRouteDefaultClickDate.setOnClickListener {
            showDatePickerDialog()
        }
        binding.imgRouteDefaultBack.setOnClickListener {
            onClickBack()
        }

    }

    private fun getscheduleList() {
        db.collection("routes").document(route.id).collection("schedules")

            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val schedule = document.toObject<Schedule>()
                    if (schedule != null) {
                        schedule.id = document.id
                        adapter.addSchedule(schedule)
                    }
                }
            }
            .addOnFailureListener { exception ->
            }
    }

    private fun onClickBack() {
        val navController = activity?.findNavController(R.id.framelayoutAdmin)
        navController?.popBackStack()
        val bottomNavigationView =
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationViewAdmin)
        bottomNavigationView?.visibility = View.VISIBLE
    }

    private fun getVehicleList() {
        db.collection("admins").document(id).collection("vehicles")
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

    private fun onClickAddschedule() {
        val dialog: Dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.admin_dialog_routes_default_add_schedule);
        dialog.show();

        var spinnerSelectBus = dialog.findViewById<Spinner>(R.id.spinnerSelectBus)
        val tvCancel = dialog.findViewById<TextView>(R.id.tvCancle)
        val tvSave = dialog.findViewById<TextView>(R.id.tvSave)
        val datePicker = dialog.findViewById<TimePicker>(R.id.datePicker)
        datePicker.is24HourView

        val adapterSpinner = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            vehicleList.map { it.licensePlate + " - " + it.type + " - " + it.seats.toString() + " chỗ ngồi" }
        )
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSelectBus.adapter = adapterSpinner

        tvSave.setOnClickListener {
            var timeRoute = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                TimeRoute(datePicker.hour.toString().toInt(), datePicker.minute.toString().toInt())
            } else {
            }

            val dateString = binding.tvRouteDefaultDate.text.toString()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            try {
                val parsedDate: Date = dateFormat.parse(dateString)

                var schedule = Schedule(
                    route.id,
                    timeRoute as TimeRoute,
                    route.departureLocation,
                    route.destination,
                    vehicleList.get(spinnerSelectBus.selectedItemPosition).id,
                    parsedDate
                )
                adapter.addSchedule(schedule)
                db.collection("routes").document(route.id).collection("schedules")
                    .add(schedule)
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(requireActivity(), "Thêm vé xe thành công", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener { e ->
                    }
                dialog.dismiss()
            } catch (e: ParseException) {
                e.printStackTrace()
            }


        }

        tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow()?.getAttributes()?.windowAnimations = R.style.DialogAnimation
        dialog.getWindow()?.setGravity(Gravity.BOTTOM);
    }

    private fun showDatePickerDialog() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = requireActivity().let {
            DatePickerDialog(it, { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                binding.tvRouteDefaultDate.text = selectedDate
            }, year, month, day)
        }
        datePickerDialog!!.show()
    }

    private fun getDate() {
        val date = Date()
        val formatDate = SimpleDateFormat("dd/MM/yyyy")
        val dateEd = formatDate.format(date)
        binding.tvRouteDefaultDate.text = dateEd
    }

}