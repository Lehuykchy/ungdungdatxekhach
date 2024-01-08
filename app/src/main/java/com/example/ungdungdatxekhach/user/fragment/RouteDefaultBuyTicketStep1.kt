package com.example.ungdungdatxekhach.user.fragment

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
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
import com.example.ungdungdatxekhach.modelshare.Location
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.model.Admin
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
import java.text.SimpleDateFormat
import java.util.Date


class RouteDefaultBuyTicketStep1 : Fragment() {
    private lateinit var binding: FragmentRouteDefaultBuyticketStep1Binding
    private lateinit var route: Route
    private lateinit var schedule: Schedule
    private lateinit var admin: Admin
    private lateinit var adapter: ItemEvaluateAdapter
    private lateinit var listItem: ArrayList<Evaluate>
    private lateinit var phone: String
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    private val db = Firebase.firestore
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

        listItem = ArrayList()

        setInfo()

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
            setInfo()
        }
        binding.tvBuyTicketStep1Evaluate.setOnClickListener {
            getListEvaluate()
            binding.tvBuyTicketStep1InfoBus.setTextColor(Color.BLACK)
            binding.lnInfoAdmin.visibility = View.GONE
            binding.lnInfoEvaluate.visibility = View.VISIBLE
            binding.tvBuyTicketStep1Evaluate.setTextColor(Color.WHITE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.tvBuyTicketStep1InfoBus.backgroundTintList = colorDilableClick
                binding.tvBuyTicketStep1Evaluate.backgroundTintList = colorClick
            }
            binding.rcvBuyTicketStep1Evaluate.layoutManager = LinearLayoutManager(requireActivity())
            adapter = ItemEvaluateAdapter(listItem, requireActivity(), object : ItemEvaluateAdapter.IClickListener{
                override fun onClick(position: Int) {

                }

            })
            binding.rcvBuyTicketStep1Evaluate.adapter = adapter
            binding.rcvBuyTicketStep1Evaluate.isNestedScrollingEnabled = false

        }
        setClickAddTicket()

        binding.btnBuyTicketStep1Confirm.setOnClickListener {
            if (ischeck()) {
                val bundle = bundleOf("route" to route, "schedule" to schedule,
                    "mount" to binding.tvBuyTicketStep1CountTicket.text.toString().toInt())
                val navController = activity?.findNavController(R.id.framelayout)
                navController?.navigate(R.id.action_routeDefaultBuyTicketStep1_to_routeDefaultBuyTicketStep2, bundle)
            }
        }

        binding.imgBuyTicketStep1BackUser.setOnClickListener {
            val navController = activity?.findNavController(R.id.framelayout)
            val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNavigationView?.visibility = View.VISIBLE
            navController?.popBackStack()
        }
        binding.lnBuyTicketStep1Schedule.setOnClickListener {
            onClickSchedule()
        }
    }

    private fun getListEvaluate() {
        listItem.clear()
        db.collection("evaluates")
            .whereEqualTo("adminId", route.idAdmin)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                for(document in documentSnapshot){
                    var evaluate = document.toObject<Evaluate>()
                    if(evaluate!=null){
                        listItem.add(evaluate)
                    }
                }
                adapter.notifyDataSetChanged()
            }

    }

    private fun setInfo() {
        binding.tvBuyTicketStep1DepartureTime.text = schedule.dateRoute.pickedHour.toString() + ":"+
                schedule.dateRoute.pickedMinute.toString() + " | " + dateFormat.format(schedule.date)
        binding.tvBuyTicketStep1Schedule.text = route.departureLocation + " - " + route.destination
        binding.tvBuyTicketStep1Distance.text = route.distance +" Km"
        binding.tvBuyTicketStep1Price.text = route.price + " đ"
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
                binding.tvBuyTicketStep1TotalMoney.setText((i * route.price.toInt()).toString() + " đ")
            }
        }

        binding.imgBuyTicketStep1AddTicket.setOnClickListener {
            val i: Int = binding.tvBuyTicketStep1CountTicket.text.toString().toInt() + 1
            binding.tvBuyTicketStep1CountTicket.setText(i.toString())
            binding.tvBuyTicketStep1CountSeat.setText(i.toString() + " vé")
            binding.tvBuyTicketStep1TotalMoney.setText((i * route.price.toInt()).toString() + " đ")
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