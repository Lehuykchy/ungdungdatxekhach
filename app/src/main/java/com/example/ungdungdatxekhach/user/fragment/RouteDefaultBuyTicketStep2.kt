package com.example.ungdungdatxekhach.user.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.Constants
import com.example.ungdungdatxekhach.databinding.FragmentRouteDefaultBuyticketStep1Binding
import com.example.ungdungdatxekhach.databinding.FragmentRouteDefaultBuyticketStep2Binding
import com.example.ungdungdatxekhach.modelshare.Location
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
import java.util.Locale

class RouteDefaultBuyTicketStep2 : Fragment() {
    private lateinit var binding: FragmentRouteDefaultBuyticketStep2Binding
    private lateinit var route: Route
    private lateinit var schedule: Schedule
    private lateinit var user: User
    private lateinit var phone: String
    private var count = 0
    private lateinit var departure: String
    private lateinit var destination: String
    private var ticket = Ticket()
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
        if (receivedBundle == null) {
            return
        }
        val i = requireActivity().intent
        phone = i.getStringExtra("phone").toString()
        route = receivedBundle.getSerializable("route") as Route
        schedule = receivedBundle.getSerializable("schedule") as Schedule
        count = receivedBundle.getSerializable("mount") as Int
        departure = route.location.get(0).other.toString()
        destination = route.location.get(route.location.size - 1).other.toString()
        setspinnerTvBuyTicketStep1Departure()
        setspinnerTvBuyTicketStep1Destination()

        try {
            db.collection("users").document(phone)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        user = document.toObject<User>()!!
                        binding.edtBuyTicketStep2Email.setText(user.email)
                        binding.edtBuyTicketStep2Name.setText(user.name)
                        binding.edtBuyTicketStep2Phone.setText(phone)
                    }
                }
                .addOnFailureListener { exception ->
                }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        binding.tvBuyTicketStep2TotalMoney.text =
            Constants.formatCurrency((count * route.price.toDouble()))
        binding.btnBuyTicketStep2Confirm.setOnClickListener {
            if (ischeck()) {
                ticket.customerId = phone
                ticket.scheduleId = schedule.id
                ticket.routeId = route.id!!
                ticket.departure = Location("","", "", binding.edtBuyTicketStep2Departure.text.toString())
                ticket.destination = Location("","", "", binding.edtBuyTicketStep2Destination.text.toString())
                ticket.count = count
                ticket.totalPrice = (count * route.price.toString().toInt()).toString()
                ticket.email = binding.edtBuyTicketStep2Email.text.toString()
                ticket.name = binding.edtBuyTicketStep2Name.text.toString()
                ticket.phone = binding.edtBuyTicketStep2Phone.text.toString()
                ticket.description = binding.edtBuyTicketStep2Description.text.toString()
                ticket.adminId = route.idAdmin
                ticket.routeId = route.id
                ticket.timeRoute = schedule.dateRoute
                ticket.dateDeparture = schedule.date
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

    private fun setspinnerTvBuyTicketStep1Destination() {
        val options = arrayOf("Tại bến", "Tại nhà")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTvBuyTicketStep2Destination.adapter = adapter
        binding.spinnerTvBuyTicketStep2Destination.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedOption = options[position]
                    when (selectedOption) {
                        "Tại bến" -> {
                            binding.edtBuyTicketStep2Destination.setText(destination)

                        }

                        "Tại nhà" -> {
                            binding.edtBuyTicketStep2Destination.setText("")
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Không có lựa chọn nào được chọn
                }
            }
    }

    private fun setspinnerTvBuyTicketStep1Departure() {
        val options = arrayOf("Tại bến", "Dọc đường")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTvBuyTicketStep2Departure.adapter = adapter
        binding.spinnerTvBuyTicketStep2Departure.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedOption = options[position]
                    when (selectedOption) {
                        "Tại bến" -> {
                            binding.edtBuyTicketStep2Departure.setText(departure)
                        }

                        "Dọc đường" -> {
                            binding.edtBuyTicketStep2Departure.setText("")
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Không có lựa chọn nào được chọn
                }
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
        if (binding.edtBuyTicketStep2Departure.text.toString().isEmpty()) {
            binding.edtBuyTicketStep2Departure.error = "Nhập điểm đi"
            return false
        }
        if (binding.edtBuyTicketStep2Destination.text.toString().isEmpty()) {
            binding.edtBuyTicketStep2Destination.error = "Nhập điểm đến"
            return false
        }
        return true
    }

}