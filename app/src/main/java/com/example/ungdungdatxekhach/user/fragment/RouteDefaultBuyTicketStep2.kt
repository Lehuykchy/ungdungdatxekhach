package com.example.ungdungdatxekhach.user.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.databinding.FragmentRouteDefaultBuyticketStep1Binding
import com.example.ungdungdatxekhach.databinding.FragmentRouteDefaultBuyticketStep2Binding
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.example.ungdungdatxekhach.user.model.Ticket
import com.example.ungdungdatxekhach.user.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class RouteDefaultBuyTicketStep2 : Fragment() {
    private lateinit var binding: FragmentRouteDefaultBuyticketStep2Binding
    private lateinit var route: Route
    private lateinit var schedule: Schedule
    private lateinit var ticket: Ticket
    private lateinit var user: User
    val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRouteDefaultBuyticketStep2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val receivedBundle = arguments
        if (receivedBundle != null && receivedBundle.containsKey("route") && receivedBundle.containsKey(
                "schedule"
            )
        ) {
            route = receivedBundle.getSerializable("route") as Route
            schedule = receivedBundle.getSerializable("schedule") as Schedule
            ticket = receivedBundle.getSerializable("ticket") as Ticket
        }
        try {
            db.collection("users").document(ticket.customerId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        user = document.toObject<User>()!!
                        binding.edtBuyTicketStep2Email.setText(user.email)
                        binding.edtBuyTicketStep2Name.setText(user.name)
                    }
                }
                .addOnFailureListener { exception ->
                }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        binding.edtBuyTicketStep2Phone.setText(ticket.customerId)
        binding.tvBuyTicketStep2Route.text = route.departureLocation + " - " + route.destination
        binding.tvBuyTicketStep2Departure.text = ticket.departure
        binding.tvBuyTicketStep2Destination.text = ticket.destination
        binding.tvBuyTicketStep2CreateAt.text =
            schedule.dateRoute.pickedHour.toString() +
                    ":" + schedule.dateRoute.pickedMinute.toString() +
                    " " + dateFormat.format(schedule.date)
        binding.tvBuyTicketStep2TotalMoney.text =
            (ticket.count.toString().toInt() * route.price.toInt()).toString() + " đ"

        binding.btnBuyTicketStep2Confirm.setOnClickListener {
            if (ischeck()) {
                ticket.email = binding.edtBuyTicketStep2Email.text.toString()
                ticket.name = binding.edtBuyTicketStep2Name.text.toString()
                ticket.phone = binding.edtBuyTicketStep2Phone.text.toString()
                ticket.description = binding.edtBuyTicketStep2Description.text.toString()
                ticket.adminId = route.idAdmin
                ticket.routeId = route.id
                val bundle = bundleOf("route" to route, "schedule" to schedule, "ticket" to ticket)
                val navController = activity?.findNavController(R.id.framelayout)
                navController?.navigate(R.id.action_routeDefaultBuyTicketStep2_to_routeDefaultBuyTicketStep3, bundle)
            }
        }
        binding.imgBuyTicketStep2Back.setOnClickListener {
            val navController = activity?.findNavController(R.id.framelayout)
            navController?.popBackStack()
        }

    }

    private fun ischeck(): Boolean {
        if (binding.edtBuyTicketStep2Phone.text.toString() == "0") {
            binding.edtBuyTicketStep2Phone.error = "Nhập thông tin"
            return false
        }
        if (binding.edtBuyTicketStep2Name.text.toString().isEmpty()) {
            binding.edtBuyTicketStep2Name.error = "Nhập thông tin"
            return false
        }
        if (binding.edtBuyTicketStep2Email.text.toString().isEmpty()) {
            binding.edtBuyTicketStep2Email.error = "Nhập thông tin"
            return false
        }
        return true
    }
}