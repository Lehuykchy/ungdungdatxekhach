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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.Constants
import com.example.ungdungdatxekhach.admin.adapter.ItemTicketOrderAdapter
import com.example.ungdungdatxekhach.databinding.AdminFragmentHomeBinding
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.example.ungdungdatxekhach.user.model.Ticket
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class AdminHomeFragment : Fragment() {
    private lateinit var binding: AdminFragmentHomeBinding
    private lateinit var adapter: ItemTicketOrderAdapter
    private lateinit var listItem: ArrayList<Ticket>
    private lateinit var listRoute: ArrayList<Route>
    private lateinit var listSchedule: ArrayList<Schedule>
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    val id = currentUser!!.uid
    private val db = Firebase.firestore
    private var selectedRoute: Route? = null
    private var selectedSchedule: Schedule? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = AdminFragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listItem = ArrayList()
        listRoute = ArrayList()
        listSchedule = ArrayList()
        getListRoute()
        getListItem()

        binding.rcvAdminHome.layoutManager = LinearLayoutManager(requireActivity())
        adapter = ItemTicketOrderAdapter(
            listItem,
            requireActivity(),
            object : ItemTicketOrderAdapter.IClickListener {
                override fun onClick(ticket: Ticket) {
                    val listSelectRoute: ArrayList<Route> = ArrayList()
                    for (route in listRoute) {
                        if (route.location.any { it.district == ticket.departure.district } && route.location.any { it.district == ticket.destination.district }) {
                            listSelectRoute.add(route)
                        }
                    }
                    setShowDialog(ticket, listSelectRoute)
                }
            })
        binding.rcvAdminHome.adapter = adapter
    }

    private fun setShowDialog(ticket: Ticket, listSelectRoute: ArrayList<Route>) {
        val dialog: Dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_admin_order);
        dialog.show();
        val ok = dialog.findViewById<Button>(R.id.btnDialogAdminOrderConfirm)
        val cancle = dialog.findViewById<Button>(R.id.btnDialogAdminOrderCancel)
        val spinnerDialogRoute = dialog.findViewById<Spinner>(R.id.spinnerDialogAdminOrderRoute)
        val spinnerDialogSchedule =
            dialog.findViewById<Spinner>(R.id.spinnerDialogAdminOrderSchedule)

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listSelectRoute.map { it.departureLocation + " - " + it.destination }
        )
        val formatDate = SimpleDateFormat("dd/MM/yyyy")

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDialogRoute.adapter = adapter
        spinnerDialogRoute.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                listSchedule.clear()
                selectedRoute = listSelectRoute[position]
                db.collection("routes").document(selectedRoute!!.id).collection("schedules")
                    .whereEqualTo("date", ticket.dateDeparture)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val schedule = document.toObject<Schedule>()
                            schedule.id = document.id
                            if (schedule != null) {
                                if (schedule.date == Date()) {
                                    if (schedule.dateRoute.pickedHour >= ticket.timeRoute.pickedHour && schedule.dateRoute.pickedMinute >= ticket.timeRoute.pickedMinute)
                                        listSchedule.add(schedule)
                                } else {
                                    listSchedule.add(schedule)
                                }
                            }
                        }
                        val adapterSchedule = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            listSchedule.map {
                                it.dateRoute.pickedHour.toString() +
                                        ":" + it.dateRoute.pickedMinute.toString() +
                                        " | " + formatDate.format(it.date)
                            }
                        )
                        adapterSchedule.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerDialogSchedule.adapter = adapterSchedule
                        adapterSchedule.notifyDataSetChanged()
                        spinnerDialogSchedule.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    selectedSchedule = listSchedule.get(position)
                                }

                                override fun onNothingSelected(p0: AdapterView<*>?) {
                                }
                            }
                    }
                    .addOnFailureListener { exception ->
                    }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        ok.setOnClickListener {
            ticket.adminId = id
            ticket.routeId = selectedRoute!!.id
            ticket.scheduleId = selectedSchedule!!.id
            ticket.totalPrice = (selectedRoute!!.price.toString().toInt() * ticket.count).toString()
            ticket.status = Constants.STATUS_WAIT_CUSTOMER
            db.collection("tickets").document(ticket.id)
                .update("status", Constants.STATUS_WAIT_CUSTOMER)
                .addOnCompleteListener { document -> }
                .addOnFailureListener { exception -> }
            db.collection("users").document(ticket.customerId).collection("tickets")
                .document(ticket.id)
                .set(ticket)
                .addOnCompleteListener { document ->
                    val bundle = bundleOf(
                        "selectedRoute" to selectedRoute,
                        "selectedSchedule" to selectedSchedule,
                        "ticket" to ticket,
                    )
                    val navController = activity?.findNavController(R.id.framelayoutAdmin)
                    val bottomNavigationView =
                        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationViewAdmin)
                    bottomNavigationView?.visibility = View.GONE
                    navController?.navigate(
                        R.id.action_navigation_home_admin_to_adminHomeOrderDefaultFragment, bundle
                    )
                    dialog.dismiss()
                }
                .addOnFailureListener { }
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

    private fun getListRoute() {
        db.collection("routes")
            .whereEqualTo("idAdmin", id)
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    val route = document.toObject<Route>()
                    route.id = document.id
                    if (route != null) {
                        listRoute.add(route)
                    }
                }
            }
            .addOnFailureListener { exception ->
            }
    }

    private fun getListItem() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        db.collection("tickets")
            .whereEqualTo("status", Constants.STATUS_SEARCH_ADMIN)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val ticket = document.toObject<Ticket>()
                    if (ticket != null && isAfterOrEqualToday(ticket.dateDeparture) && ticket.timeRoute.pickedHour >= hour
                        && ticket.timeRoute.pickedMinute >= minute
                    ) {
                        ticket.id = document.id
                        for (route in listRoute) {
                            if (route.location.any { it.district == ticket.departure.district }
                                && route.location.any { it.district == ticket.destination.district }) {
                                listItem.add(ticket)
                                break
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
            }
    }

    fun isAfterOrEqualToday(date: Date): Boolean {
        val today = Calendar.getInstance().apply { time = Date() }
        val targetDate = Calendar.getInstance().apply { time = date }

        return !targetDate.before(today) || isSameDay(date, Date())
    }

    fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }

        return cal1[Calendar.YEAR] == cal2[Calendar.YEAR] &&
                cal1[Calendar.MONTH] == cal2[Calendar.MONTH] &&
                cal1[Calendar.DAY_OF_MONTH] == cal2[Calendar.DAY_OF_MONTH]
    }

}