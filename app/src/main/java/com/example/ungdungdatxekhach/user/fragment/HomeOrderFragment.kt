package com.example.ungdungdatxekhach.user.fragment

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
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.Constants
import com.example.ungdungdatxekhach.admin.model.Admin
import com.example.ungdungdatxekhach.databinding.FragmentHomeOrderBinding
import com.example.ungdungdatxekhach.modelshare.Location
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.example.ungdungdatxekhach.modelshare.TimeRoute
import com.example.ungdungdatxekhach.user.model.Ticket
import com.example.ungdungdatxekhach.user.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date

class HomeOrderFragment : Fragment() {
    private lateinit var binding: FragmentHomeOrderBinding
    private var ticket = Ticket()
    private lateinit var phone: String
    private var route = Route()
    private var schedule = Schedule()
    private var admin = Admin()
    private val db = Firebase.firestore
    private val formatDate = SimpleDateFormat("dd/MM/yyyy")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val receivedBundle = arguments
        val receivedIntent = requireActivity().intent
        phone = receivedIntent.getStringExtra("phone") ?: ""
        if (receivedBundle != null) {
            ticket = receivedBundle.getSerializable("ticket") as Ticket
            binding.tvHomeOrderTime.text =
                ticket.timeRoute.pickedHour.toString() + ":" + ticket.timeRoute.pickedMinute.toString() +
                        " | " + formatDate.format(ticket.dateDeparture)
            binding.tvHomeOrderDepartureOther.text = ticket.departure.other
            binding.tvHomeOrderDestinationOther.text = ticket.destination.other
            binding.tvHomeOrderDeparture.text =
                ticket.departure.ward + "/" + ticket.departure.district + "/" + ticket.departure.city
            binding.tvHomeOrderDestination.text =
                ticket.destination.ward + "/" + ticket.destination.district + "/" + ticket.destination.city
            binding.tvHomeOrderNameUser.text = ticket.name.toString()
            binding.tvHomeOrderPhoneUser.text = ticket.phone.toString()
            binding.tvHomeOrderPrice.text = "x " + ticket.count.toString()

            if (ticket.status.equals(Constants.STATUS_SEARCH_ADMIN)) {
                binding.lnHomeOrderStatusWait.visibility = View.VISIBLE
                binding.lnHomeOrderStatusWaitConfirmCustomer.visibility = View.GONE
                binding.btnHomeOrderDestroyOrder.visibility = View.VISIBLE
                binding.btnHomeOrderConfirmAdmin.visibility = View.GONE
                binding.btnHomeOrderDestroyAdmin.visibility = View.GONE
                binding.btnHomeOrderDestroyOrder.setOnClickListener {
                    setOnClickDestroy()
                }
            }else if(ticket.status.equals(Constants.STATUS_WAIT_CUSTOMER)){
                binding.lnHomeOrderStatusWait.visibility = View.GONE
                binding.lnHomeOrderStatusWaitConfirmCustomer.visibility = View.VISIBLE
                binding.btnHomeOrderDestroyOrder.visibility = View.GONE
                binding.btnHomeOrderConfirmAdmin.visibility = View.VISIBLE
                binding.btnHomeOrderDestroyAdmin.visibility = View.VISIBLE
                getRoute()
                getSchedule()
                getAdmin()

                binding.tvHomeOrderNameAdmin.setOnClickListener {
                    onClickAdmin()
                }
                binding.lnHomeOrderSchedule.setOnClickListener {
                    onClickSchedule()
                }
                binding.btnHomeOrderConfirmAdmin.setOnClickListener {
                    setOnClickBtnConfirm()
                }
                binding.btnHomeOrderDestroyAdmin.setOnClickListener {
                    seOnClickDestroyAdmin()
                }

            }
        }
        binding.imgHomeOrderBack.setOnClickListener {
            onClickHomeOrderBack()
        }
    }

    private fun onClickAdmin() {
        val bundle = bundleOf("ticket" to ticket)
        Log.d("checkdb", "clickNext: " + ticket + " " + schedule)
        val navController = activity?.findNavController(R.id.framelayout)
        navController?.navigate(
            R.id.action_homeOrderFragment_to_homeOrderDefaultAdminFragment,
            bundle
        )
    }

    private fun setOnClickDestroy() {
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

        choosetxt.text = "Bạn có muốn hủy chuyến đi này không?"

        ok.setOnClickListener {
            val dataToUpdate = mapOf(
                "adminId" to "",
                "routeId" to "",
                "scheduleId" to "",
                "totalPrice" to "",
                "status" to Constants.STATUS_DESTROY
            )
            db.collection("users").document(ticket.customerId).collection("tickets").document(ticket.id)
                .update(dataToUpdate)
                .addOnCompleteListener {

                }
                .addOnFailureListener { }
            db.collection("tickets").document(ticket.id)
                .delete()
                .addOnCompleteListener {
                }
                .addOnFailureListener { }
            Toast.makeText(requireContext(), "Hủy chuyến xe thành công!", Toast.LENGTH_SHORT).show()
            onClickHomeOrderBack()
            dialog.dismiss()
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

    private fun seOnClickDestroyAdmin() {
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

        choosetxt.text = "Bạn có muốn từ chối nhà xe này không?"

        ok.setOnClickListener {
            val dataToUpdate = mapOf(
                "adminId" to "",
                "routeId" to "",
                "scheduleId" to "",
                "totalPrice" to "",
                "status" to Constants.STATUS_SEARCH_ADMIN
            )
            db.collection("users").document(ticket.customerId).collection("tickets").document(ticket.id)
                .update(dataToUpdate)
                .addOnCompleteListener { document ->
                    Toast.makeText(requireActivity(), "Từ chối nhà xe thành công!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { }
            db.collection("tickets").document(ticket.id)
                .update(dataToUpdate)
                .addOnCompleteListener { document ->
                }
                .addOnFailureListener { }
            onClickHomeOrderBack()
            dialog.dismiss()
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

        choosetxt.text = "Bạn có muốn chấp nhận chuyến đi này không?"

        ok.setOnClickListener {
            ticket.status = Constants.STATUS_WAIT_PAID
            db.collection("users").document(ticket.customerId).collection("tickets").document(ticket.id)
                .set(ticket)
                .addOnCompleteListener { document ->
                    val bundle = bundleOf(
                        "schedule" to schedule,
                        "ticket" to ticket,
                    )
                    val navController = activity?.findNavController(R.id.framelayout)
                    navController?.navigate(
                        R.id.action_homeOrderFragment_to_ordersDefaultFragment,
                        bundle
                    )
                    dialog.dismiss()
                }
                .addOnFailureListener { }
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


    private fun getAdmin() {
        db.collection("admins").document(ticket.adminId)
            .get()
            .addOnSuccessListener { document ->
                admin = document.toObject(Admin::class.java)!!
                binding.tvHomeOrderNameAdmin.text = admin.name
            }
    }

    private fun getSchedule() {
        db.collection("routes").document(ticket.routeId).collection("schedules").document(ticket.scheduleId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    schedule = document.toObject<Schedule>()!!
                    binding.tvHomeOrderTimeAdmin.text = schedule.dateRoute.pickedHour.toString() +
                            ":"+ schedule.dateRoute.pickedMinute.toString() +
                            " | " + formatDate.format(schedule.date)

                }
            }
            .addOnFailureListener { exception ->
            }
    }

    private fun getRoute() {
        db.collection("routes").document(ticket.routeId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    route = document.toObject<Route>()!!
                    binding.tvHomeOrderPriceAdmin.text = route.price +"đ"
                    binding.tvHomeOrderDepartureAdmin.text = route.departureLocation
                    binding.tvHomeOrderDestinationAdmin.text = route.destination

                }
            }
            .addOnFailureListener { exception ->
            }

    }

    private fun onClickSchedule() {
        val dialog: Dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_schedule);
        dialog.show();

        val cancle: TextView
        val list: ListView

        cancle = dialog.findViewById(R.id.tvLayoutDialogScheduleCancel)
        list = dialog.findViewById(R.id.lwLayoutDialogSchedule)

        val locationAdapter =
            RouteDefaultBuyTicketStep1.LocationAdapter(requireActivity(), route.location)
        list.adapter = locationAdapter

        cancle.setOnClickListener {
            dialog.dismiss()
        }


        dialog.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow()?.getAttributes()?.windowAnimations = R.style.DialogAnimation
        dialog.getWindow()?.setGravity(Gravity.BOTTOM);
    }


    private fun onClickHomeOrderBack() {
        val navController = activity?.findNavController(R.id.framelayout)
        navController?.popBackStack()
        val bottomNavigationView =
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView?.visibility = View.VISIBLE
    }


}