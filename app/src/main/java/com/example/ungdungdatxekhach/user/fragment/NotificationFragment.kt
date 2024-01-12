package com.example.ungdungdatxekhach.user.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.databinding.FragmentNotificationBinding
import com.example.ungdungdatxekhach.modelshare.Notification
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.example.ungdungdatxekhach.modelshare.adapter.ItemNotificationAdapter
import com.example.ungdungdatxekhach.user.model.Ticket
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class NotificationFragment : Fragment() {
    private lateinit var binding: FragmentNotificationBinding
    private lateinit var adapter: ItemNotificationAdapter
    private lateinit var listItem: ArrayList<Notification>
    private var ticket = Ticket()
    private var schedule = Schedule()
    private val db = Firebase.firestore
    private lateinit var phone: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listItem = ArrayList()
        getListNotification()
        binding.rcvNotification.layoutManager = LinearLayoutManager(requireActivity())
        adapter = ItemNotificationAdapter(listItem, requireActivity(), object: ItemNotificationAdapter.IClickListener{
            override fun onClick(position: Int) {
                getSchedule(listItem.get(position))
            }

        })
        binding.rcvNotification.adapter = adapter
    }

    private fun getSchedule(notification: Notification) {
        db.collection("users").document(phone).collection("tickets").document(notification.ticketId)
            .get()
            .addOnSuccessListener { document ->
                ticket = document.toObject<Ticket>()!!
                if(ticket == null){
                    return@addOnSuccessListener
                }
                db.collection("routes").document(ticket.routeId).collection("schedules").document(ticket.scheduleId)
                    .get()
                    .addOnSuccessListener { document ->
                        schedule = document.toObject<Schedule>()!!
                        if(schedule == null){
                            return@addOnSuccessListener
                        }
                        val bundle = bundleOf("schedule" to schedule, "ticket" to ticket)
                        val navController = activity?.findNavController(R.id.framelayout)
                        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                        bottomNavigationView?.visibility = View.GONE
                        navController?.navigate(
                            R.id.action_navigation_notification_to_ordersDefaultFragment,
                            bundle
                        )
                    }
            }
    }

    private fun getListNotification() {
        val i = requireActivity().intent
        phone = i.getStringExtra("phone").toString()
        db.collection("users").document(phone).collection("notifications")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                for(document in documentSnapshot){
                    var notification = document.toObject<Notification>()
                    if(notification!=null){
                        listItem.add(notification)
                    }
                }
                adapter.notifyDataSetChanged()
                if(listItem.size>0){
                    binding.lnNoData.visibility=View.GONE
                }else{
                    binding.lnNoData.visibility=View.VISIBLE
                }
            }
    }
}