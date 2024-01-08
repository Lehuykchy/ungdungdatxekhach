package com.example.ungdungdatxekhach.admin.fragment

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.Constants
import com.example.ungdungdatxekhach.admin.adapter.ItemTicketManagerAdapter
import com.example.ungdungdatxekhach.databinding.AdminFragmentRoutesDefaultTicketBinding
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.example.ungdungdatxekhach.user.model.Ticket
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class AdminRouteDefaultTicketFragment : Fragment() {
    lateinit var binding: AdminFragmentRoutesDefaultTicketBinding
    private var schedule = Schedule()
    private lateinit var listTicket: ArrayList<Ticket>
    private lateinit var adapter : ItemTicketManagerAdapter
    private val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AdminFragmentRoutesDefaultTicketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val receivedBundle = arguments
        if (receivedBundle == null) {
           return
        }
        schedule = receivedBundle.getSerializable("schedule") as Schedule
        val currentTime = Calendar.getInstance()
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentTime.get(Calendar.MINUTE)
        var time = schedule.dateRoute.pickedHour*60 + schedule.dateRoute.pickedMinute -
                currentHour*60-currentMinute
        if(time <= 15  && schedule.status.equals(Constants.STATUS_NO_START)){
            Log.d("checkstatus", "chưa bắt đầu ")
            binding.btnOrderDefaultConfirm.isEnabled = true
            binding.btnOrderDefaultConfirm.text = "Bắt đầu xuất phát"
            ColorStateList.valueOf(android.graphics.Color.parseColor("#00cba9"))
        }else {
            if (schedule.status.equals(Constants.STATUS_FINISH)) {
                Log.d("checkstatus", "đã kết thúc ")
                binding.btnOrderDefaultConfirm.isEnabled = false
                binding.btnOrderDefaultConfirm.text = "Chuyến đi kết thúc"
            } else {
                Log.d(
                    "checkstatus",
                    "chưa xuất phát được " + time + "-" + schedule.dateRoute.pickedHour * 60 + schedule.dateRoute.pickedMinute + "-" + currentHour * 60 + currentMinute
                )
                binding.btnOrderDefaultConfirm.isEnabled = false
                binding.btnOrderDefaultConfirm.text = "Bắt đầu xuất phát"
                setColor(ColorStateList.valueOf(android.graphics.Color.parseColor("#a5a5a5")))
            }
        }
        listTicket = ArrayList()
        listTicket = schedule.customerIds

        binding.rcvRouteDefaultTicket.layoutManager = LinearLayoutManager(requireActivity())
        adapter = ItemTicketManagerAdapter(listTicket, requireActivity(), object: ItemTicketManagerAdapter.IClickListener{
            override fun onClick(ticket: Ticket) {

            }

        })
        binding.rcvRouteDefaultTicket.adapter = adapter

        binding.imgRouteDefaultTicketBack.setOnClickListener {
            val navController = activity?.findNavController(R.id.framelayoutAdmin)
            navController?.popBackStack()
        }
    }

    fun setColor( color : ColorStateList){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.btnOrderDefaultConfirm.backgroundTintList = color
        }
    }
}