package com.example.ungdungdatxekhach.user.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.Constants
import com.example.ungdungdatxekhach.admin.model.Vehicle
import com.example.ungdungdatxekhach.databinding.FragmentOrderDefaultBinding
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.example.ungdungdatxekhach.modelshare.activity.LoginActivity
import com.example.ungdungdatxekhach.user.model.Ticket
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class OrdersDefaultFragment : Fragment() {
    private lateinit var binding: FragmentOrderDefaultBinding
    private var schedule = Schedule()
    private var ticket = Ticket()
    private var vehicle = Vehicle()
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDefaultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val receivedBundle = arguments
        if (!(receivedBundle != null && receivedBundle.containsKey("schedule")))
            return

        schedule = receivedBundle.getSerializable("schedule") as Schedule
        ticket = receivedBundle.getSerializable("ticket") as Ticket
        Log.d("checkdb", "clickNext: " + ticket + " " + schedule)

        if(ticket.status.equals(Constants.STATUS_PAID)){
            binding.btnOrderDefaultConfirm.visibility = View.GONE
        }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        binding.tvOrderDefaultDepartureDate.text = schedule.dateRoute.pickedHour.toString() + ":" +
                schedule.dateRoute.pickedMinute.toString() +" | "+ dateFormat.format(schedule.date)
            .toString()
        binding.tvOrderDefaultTotalMoney.text =
            ticket.totalPrice.toString() + " đ"
        binding.tvOrderDefaultDepartureLocation.text = schedule.departureLocation
        binding.tvOrderDefaultDestinationLocation.text = schedule.destinationLocation
        binding.tvOrderDefaultDepartureMyLocation.text = ticket.departure.other
        binding.tvOrderDefaultDestinationMyLocation.text = ticket.destination.other
        binding.tvOrderDefaultName.text = ticket.name
        binding.tvOrderDefaultEmail.text = ticket.email
        binding.tvOrderDefaultPhone.text = ticket.phone
        binding.tvOrderDefaultMountTicket.text = ticket.count.toString() + " vé"

        binding.imgOrderDefaultBack.setOnClickListener {
            val navController = activity?.findNavController(R.id.framelayout)
            val bottomNavigationView =
                activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNavigationView?.visibility = View.VISIBLE
            navController?.popBackStack()
        }

        getVehicle()

        binding.btnOrderDefaultConfirm.setOnClickListener {
            setOnClickBtnConfirm()
        }
    }

    private fun setOnClickBtnConfirm() {
        val dialog: Dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_bottom_sheet);
        dialog.show();

        val ok: TextView
        val cancle: TextView
        val choosetxt: TextView

        choosetxt = dialog.findViewById(R.id.tvBottomSheetChoosetxt)
        ok = dialog.findViewById(R.id.tvBottomSheetOk)
        cancle = dialog.findViewById(R.id.tvBottomSheetCancle)

        choosetxt.text = "Bạn có muốn thanh toán ${ticket.totalPrice.toString()} đ không?"

        ok.setOnClickListener {
            val intent = requireActivity().intent
            if (intent.getStringExtra("phone") != null) {
                db.collection("users").document(intent.getStringExtra("phone").toString()).collection("tickets").document(ticket.id)
                    .update("status", Constants.STATUS_PAID)
                    .addOnSuccessListener { document ->
                        Toast.makeText(requireActivity(), "Quý khách dã thanh toán thành công!", Toast.LENGTH_SHORT).show()
                        binding.btnOrderDefaultConfirm.visibility= View.GONE
                        dialog.dismiss()
                    }.addOnFailureListener { exception ->
                    }
            }
        }

        cancle.setOnClickListener {
            dialog.dismiss()
        }

        dialog.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow()?.getAttributes()?.windowAnimations = R.style.DialogAnimation
        dialog.getWindow()?.setGravity(Gravity.BOTTOM);
    }

    private fun getVehicle() {
        db.collection("admins").document(ticket.adminId).collection("vehicles")
            .document(schedule.vehicleId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    vehicle = document?.toObject<Vehicle>()!!
                    binding.tvOrderDefaultLicencePlate.text = vehicle.licensePlate
                    binding.tvOrderDefaultTypeVehicle.text =
                        vehicle.type + " " + vehicle.seats.toString()
                } else {
                }
            }.addOnFailureListener { exception ->
            }
    }
}