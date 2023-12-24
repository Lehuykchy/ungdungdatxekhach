package com.example.ungdungdatxekhach.user.fragment

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.databinding.FragmentRouteDefaultBuyticketStep3Binding
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.example.ungdungdatxekhach.modelshare.activity.LoginActivity
import com.example.ungdungdatxekhach.user.activity.MainActivity
import com.example.ungdungdatxekhach.user.model.Ticket
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class RouteDefaultBuyTicketStep3 : Fragment() {
    private lateinit var binding: FragmentRouteDefaultBuyticketStep3Binding
    private var countdownTimer: CountDownTimer? = null
    private val COUNTDOWN_TIME = TimeUnit.MINUTES.toMillis(15)
    private lateinit var route: Route
    private lateinit var schedule: Schedule
    private lateinit var ticket: Ticket
    val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRouteDefaultBuyticketStep3Binding.inflate(inflater, container, false)
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
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            binding.tvBuyTicketStep3DepartureDate.text = dateFormat.format(schedule.date).toString()
            binding.tvBuyTicketStep3TotalMoney.text =
                (ticket.count * route.price.toString().toInt()).toString() + " đ"
            binding.tvBuyTicketStep3DepartureLocation.text = route.departureLocation
            binding.tvBuyTicketStep3DestinationLocation.text = route.destination
            binding.tvBuyTicketStep3DepartureMyLocation.text = ticket.departure
            binding.tvBuyTicketStep3DestinationMyLocation.text = ticket.destination
            binding.tvBuyTicketStep3Name.text = ticket.name
            binding.tvBuyTicketStep3Email.text = ticket.email
            binding.tvBuyTicketStep3Phone.text = ticket.phone
            binding.tvBuyTicketStep3MountTicket.text = ticket.count.toString() + " vé"
            binding.tvBuyTicketStep3TotalMoneyMain.text =
                (ticket.count * route.price.toString().toInt()).toString() + " đ"
        }

        binding.imgBuyTicketStep3NameBack.setOnClickListener {
            val navController = activity?.findNavController(R.id.framelayout)
            val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNavigationView?.visibility = View.VISIBLE
            navController?.navigate(R.id.action_routeDefaultBuyTicketStep3_to_navigation_home)
        }
        startCountdownTimer()
    }

    private fun startCountdownTimer() {
        countdownTimer = object : CountDownTimer(COUNTDOWN_TIME, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                val formattedTime = String.format("%02d:%02d", minutes, seconds)
                val countdownText =
                    ("Thanh toán trong vòng $formattedTime").toString() + "\nVé sẽ bị hủy nếu không thanh toán"
                binding.tvBuyTicketStep3NameCountTime.text = countdownText
            }

            override fun onFinish() {
                binding.tvBuyTicketStep3NameCountTime.text = "00:00"
                cancelTicket()
            }
        }

        countdownTimer?.start()

        val i = requireActivity().intent
        val phone = i.getStringExtra("phone").toString()
        db.collection("users").document(phone).collection("tickets")
            .add(ticket)
            .addOnSuccessListener { document ->

            }.addOnFailureListener { exception ->
            }
        schedule.customerIds.add(ticket)
        val updateMap: Map<String, Any> = mapOf(
            "customerIds" to schedule.customerIds
        )
        db.collection("routes").document(route.id).collection("schedules").document(schedule.id)
            .update(updateMap)
            .addOnSuccessListener { document ->

            }.addOnFailureListener { exception ->
            }


    }

    private fun cancelTicket() {

    }

    override fun onDestroy() {
        super.onDestroy()

        countdownTimer?.cancel()
    }
}