package com.example.ungdungdatxekhach.admin.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.model.Vehicle
import com.example.ungdungdatxekhach.databinding.AdminFragmentHomeOrderDefaultBinding
import com.example.ungdungdatxekhach.modelshare.Location
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.example.ungdungdatxekhach.modelshare.TimeRoute
import com.example.ungdungdatxekhach.user.model.Ticket
import com.example.ungdungdatxekhach.user.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date

class AdminHomeOrderDefaultFragment : Fragment() {
    private lateinit var binding: AdminFragmentHomeOrderDefaultBinding
    private var route = Route()
    private var schedule = Schedule()
    private var ticket = Ticket()
    private val db = Firebase.firestore
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AdminFragmentHomeOrderDefaultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val receivedBundle = arguments
        if (receivedBundle != null) {
            route = receivedBundle.getSerializable("selectedRoute") as Route
            schedule = receivedBundle.getSerializable("selectedSchedule") as Schedule
            ticket = receivedBundle.getSerializable("ticket") as Ticket
            val formatDate = SimpleDateFormat("dd/MM/yyyy")
            binding.tvAdminHomeOrderDefaultName.text = currentUser!!.email.toString()
            binding.tvAdminHomeOrderDefaultDepartureDate.text =
                formatDate.format(ticket.dateDeparture)
            binding.tvAdminHomeOrderDefaultTotalMoney.text = ticket.totalPrice.toString() + " đ"
            binding.tvAdminHomeOrderDefaultCustomer.text = ticket.name
            binding.tvAdminHomeOrderDefaultPhoneCustomer.text = ticket.phone
            binding.tvAdminHomeOrderDefaultEmailCustomer.text = ticket.email
            binding.tvAdminHomeOrderDefaultMountTicket.text = ticket.count.toString()
            binding.tvAdminHomeOrderDefaultDepartureLocation.text = route.departureLocation
            binding.tvAdminHomeOrderDefaultDestinationLocation.text = route.destination
            binding.tvAdminHomeOrderDefaultDepartureCustomerLocation.text = ticket.departure.city + "/" + ticket.departure.district +
                    "/" + ticket.departure.ward + "/" +ticket.departure.other
            binding.tvAdminHomeOrderDefaultDestinationCustomerLocation.text = ticket.destination.city + "/" + ticket.destination.district +
                    "/" + ticket.destination.ward + "/" +ticket.destination.other
            binding.tvAdminHomeOrderDefaultTotalMoney.text =
                (route.price.toString().toInt()).toString() + " đ" +
                        " x" + ticket.count.toString() + " vé"
            getVehicle(schedule.vehicleId)
        }
        binding.imgAdminHomeOrderDefaultBack.setOnClickListener {
            setOnClickBack()
        }
    }

    private fun getVehicle(vehicleId: String) {
        db.collection("admins").document(currentUser!!.uid).collection("vehicles")
            .document(vehicleId)
            .get()
            .addOnSuccessListener { document ->
                val vehicle = document.toObject<Vehicle>()!!
                binding.tvAdminHomeOrderDefaultLicencePlate.text = vehicle.licensePlate
                binding.tvAdminHomeOrderDefaultTypeVehicle.text = vehicle.type

            }
            .addOnFailureListener { exception ->

            }
    }

    private fun setOnClickBack() {
        val navController = activity?.findNavController(R.id.framelayoutAdmin)
        navController?.popBackStack()
        val bottomNavigationView =
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationViewAdmin)
        bottomNavigationView?.visibility = View.VISIBLE
    }
}