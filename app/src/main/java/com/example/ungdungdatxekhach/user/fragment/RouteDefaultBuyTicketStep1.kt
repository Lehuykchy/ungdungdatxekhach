package com.example.ungdungdatxekhach.user.fragment

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ungdungdatxekhach.modelshare.Location
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.Constants
import com.example.ungdungdatxekhach.admin.model.Admin
import com.example.ungdungdatxekhach.admin.model.Vehicle
import com.example.ungdungdatxekhach.databinding.FragmentRouteDefaultBuyticketStep1Binding
import com.example.ungdungdatxekhach.modelshare.Evaluate
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.example.ungdungdatxekhach.modelshare.adapter.ItemEvaluateAdapter
import com.example.ungdungdatxekhach.user.model.Ticket
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date


class RouteDefaultBuyTicketStep1 : Fragment() {
    private lateinit var binding: FragmentRouteDefaultBuyticketStep1Binding
    private lateinit var route: Route
    private lateinit var schedule: Schedule
    private lateinit var admin: Admin
    private lateinit var vehicle: Vehicle
    private lateinit var adapter: ItemEvaluateAdapter
    private lateinit var listItem: ArrayList<Evaluate>
    private lateinit var phone: String
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    private val db = Firebase.firestore
    private var count = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRouteDefaultBuyticketStep1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val receivedBundle = arguments
        if (receivedBundle == null) {
            return
        }
        route = receivedBundle.getSerializable("route") as Route
        schedule = receivedBundle.getSerializable("schedule") as Schedule
        admin = receivedBundle.getSerializable("admin") as Admin
        vehicle = receivedBundle.getSerializable("vehicle") as Vehicle
        listItem = ArrayList()
        count = vehicle.seats
        for (ticket in schedule.customerIds) {
            count -= ticket.count
        }
        binding.tvBuyTicketStep1Blank.text = count.toString() +
                "/" + vehicle.seats.toString() + " chỗ trống"
        getListEvaluate()
        setInfo()
        setImage()


        val colorClick = ColorStateList.valueOf(android.graphics.Color.parseColor("#00cba9"))
        val colorDilableClick = ColorStateList.valueOf(android.graphics.Color.parseColor("#D2E4E1"))

        binding.tvBuyTicketStep1InfoBus.setOnClickListener {
            binding.tvBuyTicketStep1InfoBus.setTextColor(Color.WHITE)
            binding.lnInfoAdmin.visibility = View.VISIBLE
            binding.lnInfoEvaluate.visibility = View.GONE
            binding.tvBuyTicketStep1Evaluate.setTextColor(Color.BLACK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.tvBuyTicketStep1InfoBus.backgroundTintList = colorClick
                binding.tvBuyTicketStep1Evaluate.backgroundTintList = colorDilableClick
            }
        }
        binding.tvBuyTicketStep1Evaluate.setOnClickListener {
            binding.tvBuyTicketStep1InfoBus.setTextColor(Color.BLACK)
            binding.lnInfoAdmin.visibility = View.GONE
            binding.lnInfoEvaluate.visibility = View.VISIBLE
            binding.tvBuyTicketStep1Evaluate.setTextColor(Color.WHITE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.tvBuyTicketStep1InfoBus.backgroundTintList = colorDilableClick
                binding.tvBuyTicketStep1Evaluate.backgroundTintList = colorClick
            }
            binding.rcvBuyTicketStep1Evaluate.layoutManager = LinearLayoutManager(requireActivity())
            adapter = ItemEvaluateAdapter(
                listItem,
                requireActivity(),
                object : ItemEvaluateAdapter.IClickListener {
                    override fun onClick(position: Int) {

                    }

                })
            binding.rcvBuyTicketStep1Evaluate.adapter = adapter
            binding.rcvBuyTicketStep1Evaluate.isNestedScrollingEnabled = false

        }
        setClickAddTicket()

        binding.btnBuyTicketStep1Confirm.setOnClickListener {
            if (ischeck()) {
                val bundle = bundleOf(
                    "route" to route, "schedule" to schedule,
                    "mount" to binding.tvBuyTicketStep1CountTicket.text.toString().toInt()
                )
                val navController = activity?.findNavController(R.id.framelayout)
                navController?.navigate(
                    R.id.action_routeDefaultBuyTicketStep1_to_routeDefaultBuyTicketStep2,
                    bundle
                )
            }
        }

        binding.imgBuyTicketStep1BackUser.setOnClickListener {
            val navController = activity?.findNavController(R.id.framelayout)
            val bottomNavigationView =
                activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNavigationView?.visibility = View.VISIBLE
            navController?.popBackStack()
        }
        binding.lnBuyTicketStep1Schedule.setOnClickListener {
            onClickSchedule()
        }
    }

    private fun setImage() {
        val storagePath = "images/" + admin.imageBackGroundId //
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child(storagePath)
        Log.d("Firebase Storage", "setImage: " + storageRef)
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            val downloadUrl = uri.toString()
            Glide.with(this)
                .load(downloadUrl)
                .error(R.drawable.baseline_image_24)
                .into(binding.imgBuyTicketStep1Admin)
        }.addOnFailureListener { exception ->
            Log.e("Firebase Storage", "Error getting download URL: ${exception.message}")
        }
    }

    private fun getListEvaluate() {
        val decimalFormat = DecimalFormat("#.#")
        db.collection("evaluates")
            .whereEqualTo("adminId", route.idAdmin)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                for (document in documentSnapshot) {
                    var evaluate = document.toObject<Evaluate>()
                    if (evaluate != null) {
                        listItem.add(evaluate)
                    }
                }

                if (listItem.size > 0) {
                    val oneStar = listItem.filter { evaluate -> evaluate.evaluate == 1 }.size
                    val trueStar = listItem.filter { evaluate -> evaluate.evaluate == 2 }.size
                    val threeStar = listItem.filter { evaluate -> evaluate.evaluate == 3 }.size
                    val fourStar = listItem.filter { evaluate -> evaluate.evaluate == 4 }.size
                    val fiveStar = listItem.filter { evaluate -> evaluate.evaluate == 5 }.size

                    binding.tvBuyTicketStep1Star1.text = oneStar.toString()
                    binding.tvBuyTicketStep1Star2.text = trueStar.toString()
                    binding.tvBuyTicketStep1Star3.text = threeStar.toString()
                    binding.tvBuyTicketStep1Star4.text = fourStar.toString()
                    binding.tvBuyTicketStep1Star5.text = fiveStar.toString()
                    binding.tvBuyTicketStep1AdminEvaluate.text = decimalFormat.format(
                        5 * (1 * oneStar + 2 * trueStar + 3 * threeStar + 4 * fourStar + fiveStar * 5) / (5 * listItem.size).toDouble()
                    ).toString() + "/5.0"
                } else {
                    binding.tvBuyTicketStep1AdminEvaluate.text = "5.0/5.0"
                }

            }

    }

    private fun setInfo() {
        binding.tvBuyTicketStep1DepartureTime.text =
            schedule.dateRoute.toFormattString() + " | " + dateFormat.format(schedule.date)
        binding.tvBuyTicketStep1Schedule.text = route.departureLocation + " - " + route.destination
        binding.tvBuyTicketStep1Distance.text = route.distance + " Km"
        binding.tvBuyTicketStep1Price.text = Constants.formatCurrency(route.price.toDouble())
        binding.tvBuyTicketStep1AdminName.text = admin.name
        binding.tvBuyTicketStep1AdminPhone.text = admin.phone
        binding.tvBuyTicketStep1AdminEmail.text = admin.email
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

        val locationAdapter = LocationAdapter(requireActivity(), route.location)
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

    private fun ischeck(): Boolean {
        if (binding.tvBuyTicketStep1CountTicket.text.toString() == "0") {
            Toast.makeText(requireActivity(), "Chọn số vé", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun setClickAddTicket() {
        binding.imgBuyTicketStep1MinusTicket.setOnClickListener {
            if (binding.tvBuyTicketStep1CountTicket.text.toString().toInt() > 0) {
                val i: Int = binding.tvBuyTicketStep1CountTicket.text.toString().toInt() - 1
                binding.tvBuyTicketStep1CountTicket.setText(i.toString())
                binding.tvBuyTicketStep1CountSeat.setText(i.toString())
                binding.tvBuyTicketStep1TotalMoney.setText(Constants.formatCurrency((i * route.price.toDouble())))
            }
        }

        binding.imgBuyTicketStep1AddTicket.setOnClickListener {
            val i: Int = binding.tvBuyTicketStep1CountTicket.text.toString().toInt() + 1
            if (i > count) {
                Toast.makeText(requireActivity(), "Số vé không đủ!", Toast.LENGTH_SHORT).show()
            } else {
                binding.tvBuyTicketStep1CountTicket.setText(i.toString())
                binding.tvBuyTicketStep1CountSeat.setText(i.toString() + " vé")
                binding.tvBuyTicketStep1TotalMoney.setText(Constants.formatCurrency((i * route.price.toDouble())))
            }
        }
    }

    class LocationAdapter(context: Context, private val locations: List<Location>) :
        ArrayAdapter<Location>(context, android.R.layout.simple_list_item_1, locations) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            val location = getItem(position)

            val locationName = location?.other
            val textView = view.findViewById<TextView>(android.R.id.text1)
            textView.text = locationName

            return view
        }
    }

}