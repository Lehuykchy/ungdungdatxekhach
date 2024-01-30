package com.example.ungdungdatxekhach.user.fragment

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.Constants
import com.example.ungdungdatxekhach.admin.model.Admin
import com.example.ungdungdatxekhach.admin.model.Vehicle
import com.example.ungdungdatxekhach.databinding.FragmentRouteDefaultBuyticketStep3Binding
import com.example.ungdungdatxekhach.modelshare.Notification
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.example.ungdungdatxekhach.user.model.Ticket
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

class RouteDefaultBuyTicketStep3 : Fragment() {
    private lateinit var binding: FragmentRouteDefaultBuyticketStep3Binding
    private var countdownTimer: CountDownTimer? = null
    private val COUNTDOWN_TIME = TimeUnit.MINUTES.toMillis(1)
    private lateinit var route: Route
    private lateinit var schedule: Schedule
    private lateinit var ticket: Ticket
    private lateinit var admin : Admin
    val db = Firebase.firestore
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")
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
            admin = receivedBundle.getSerializable("admin") as Admin
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            setVehicle()
            binding.tvAdminName.text = admin.name
            binding.tvBuyTicketStep3DepartureDate.text = dateFormat.format(schedule.date).toString()
            binding.tvBuyTicketStep3TotalMoney.text =
                Constants.formatCurrency(ticket.count * route.price.toString().toInt().toDouble())
            binding.tvBuyTicketStep3DepartureLocation.text = route.departureLocation
            binding.tvBuyTicketStep3DestinationLocation.text = route.destination
            binding.tvBuyTicketStep3DepartureMyLocation.text = ticket.departure.other
            binding.tvBuyTicketStep3DestinationMyLocation.text = ticket.destination.other
            binding.tvBuyTicketStep3Name.text = ticket.name
            binding.tvBuyTicketStep3Email.text = ticket.email
            binding.tvBuyTicketStep3Phone.text = ticket.phone
            binding.tvBuyTicketStep3MountTicket.text = ticket.count.toString() + " vé"
            binding.tvBuyTicketStep3TotalMoneyMain.text =
                Constants.formatCurrency(ticket.count * route.price.toString().toInt().toDouble())
            binding.btnBuyTicketStep3Confirm.setOnClickListener {
                setOnClickBtnConfirm()
            }
        }

        binding.imgBuyTicketStep3NameBack.setOnClickListener {
            setOnBackHome()
        }
        startCountdownTimer()
    }

    private fun setOnBackHome() {
        val navController = activity?.findNavController(R.id.framelayout)
        navController?.navigate(R.id.action_routeDefaultBuyTicketStep3_to_navigation_home)
    }

    private fun setOnClickBtnConfirm() {
        val dialogPayment: Dialog = Dialog(requireActivity())
        dialogPayment.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPayment.setContentView(R.layout.layout_loading_payment);
        dialogPayment.setCanceledOnTouchOutside(false)

        val progressBar: ProgressBar
        val lnPaymentSuccess: LinearLayout
        val btnGoHome: AppCompatButton
        progressBar = dialogPayment.findViewById(R.id.progressBar)
        lnPaymentSuccess = dialogPayment.findViewById(R.id.lnPaymentSuccess)
        btnGoHome = dialogPayment.findViewById(R.id.btnGoHome)

        btnGoHome.setOnClickListener {
            setOnBackHome()
            dialogPayment.dismiss()
        }

        dialogPayment.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogPayment.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialogPayment.getWindow()?.getAttributes()?.windowAnimations = R.style.DialogAnimation
        dialogPayment.getWindow()?.setGravity(Gravity.CENTER);
        // mở dialog thanh tóan
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

        choosetxt.text = "Bạn có muốn thanh toán ${Constants.formatCurrency(ticket.totalPrice.toDouble())} không?"

        ok.setOnClickListener {
            dialog.dismiss()
            dialogPayment.show()
            db.collection("users").document(ticket.customerId).collection("tickets")
                .document(ticket.id)
                .update("status", Constants.STATUS_PAID)
                .addOnSuccessListener { document ->
                    progressBar.visibility = View.GONE
                    lnPaymentSuccess.visibility = View.VISIBLE
                    countdownTimer?.cancel()
                }.addOnFailureListener { exception ->
                }
            db.collection("routes").document(ticket.routeId).collection("schedules")
                .document(ticket.scheduleId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val newschedule = documentSnapshot.toObject(Schedule::class.java)
                        if (newschedule != null) {
                            newschedule.customerIds.add(ticket)
                            db.collection("routes").document(ticket.routeId)
                                .collection("schedules").document(ticket.scheduleId)
                                .update("customerIds", newschedule.customerIds)
                                .addOnSuccessListener {
                                }
                                .addOnFailureListener { e ->
                                }
                        }
                    }
                }
            var notificationInfo =
                "Bạn đã thanh toán ${ticket.count.toString()} vé xe thành công tuyến: ${route.departureLocation} - ${route.destination}" +
                        " của nhà xe.... Khởi hành: ${ticket.timeRoute.pickedHour.toString()}:${ticket.timeRoute.pickedMinute.toString()} | ${
                            dateFormat.format(ticket.dateDeparture)
                        } Xin cám ơn!!"
            val notification = Notification(ticket.id, notificationInfo, Date())
            db.collection("users").document(ticket.customerId).collection("notifications")
                .add(notification)
                .addOnSuccessListener { document ->
                }.addOnFailureListener { exception ->
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

    private fun setVehicle() {
        db.collection("admins").document(route.idAdmin).collection("vehicles")
            .document(schedule.vehicleId)
            .get()
            .addOnSuccessListener { document ->
                var vehicle = document.toObject<Vehicle>()
                binding.tvBuyTicketStep3Plates.text = vehicle?.licensePlate
                binding.tvBuyTicketStep3Type.text = vehicle?.type + " " + vehicle?.seats.toString()
            }
            .addOnFailureListener { }
    }

    private fun startCountdownTimer() {
        countdownTimer = object : CountDownTimer(COUNTDOWN_TIME, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                val formattedTime = String.format("%02d:%02d", minutes, seconds)
                val countdownText =
                    ("Thanh toán (Còn lại $formattedTime)").toString()
                binding.btnBuyTicketStep3Confirm.text = countdownText
            }

            override fun onFinish() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    binding.btnBuyTicketStep3Confirm.backgroundTintList =
                        ColorStateList.valueOf(android.graphics.Color.parseColor("#A5A5A5"))
                    binding.btnBuyTicketStep3Confirm.text = "Thanh toán"
                    binding.btnBuyTicketStep3Confirm.isEnabled = false
                }

                binding.tvBuyTicketStep3Status.text = "Đã hết hạn"
                binding.tvBuyTicketStep3Status.setTextColor(Color.RED)
                db.collection("users").document(ticket.customerId).collection("tickets")
                    .document(ticket.id)
                    .update("status", Constants.STATUS_TIMEOUT)
                    .addOnSuccessListener { document ->
                    }.addOnFailureListener { exception ->
                    }
            }
        }

        countdownTimer?.start()

        ticket.status = Constants.STATUS_WAIT_PAID
        db.collection("users").document(ticket.customerId).collection("tickets")
            .add(ticket)
            .addOnSuccessListener { document ->
                ticket.id = document.id
                var notificationInfo =
                    "Bạn đã đặt ${ticket.count.toString()} vé xe thành công tuyến: ${route.departureLocation} - ${route.destination}" +
                            " của nhà xe.... Khởi hành: ${ticket.timeRoute.pickedHour.toString()}:${ticket.timeRoute.pickedMinute.toString()} | ${
                                dateFormat.format(ticket.dateDeparture)
                            }. Vui lòng thanh toán trước 15 phút nếu không vé sẽ bị hủy. Xin cám ơn!!"
                val notification = Notification(ticket.id, notificationInfo, Date())
                db.collection("users").document(ticket.customerId).collection("notifications")
                    .add(notification)
                    .addOnSuccessListener { document ->
                    }.addOnFailureListener { exception ->
                    }
            }.addOnFailureListener { exception ->
            }
//        schedule.customerIds.add(ticket)
//        val updateMap: Map<String, Any> = mapOf(
//            "customerIds" to schedule.customerIds
//        )


    }

//    override fun onDestroy() {
//        super.onDestroy()
//
//        countdownTimer?.cancel()
//    }
}