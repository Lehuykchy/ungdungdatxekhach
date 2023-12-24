package com.example.ungdungdatxekhach.user.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.databinding.FragmentOrdersBinding
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.example.ungdungdatxekhach.user.adapter.ItemTicketAdapter
import com.example.ungdungdatxekhach.user.model.Ticket
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OrdersFragment : Fragment() {
    private lateinit var binding: FragmentOrdersBinding
    private lateinit var listTicket: ArrayList<Ticket>
    private lateinit var adapter: ItemTicketAdapter
    private val db = Firebase.firestore
    private lateinit var phone:String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val i = requireActivity().intent
        phone = i.getStringExtra("phone").toString()
        listTicket = ArrayList()
        getListTicket()
        binding.rcvOrders.layoutManager = LinearLayoutManager(requireActivity())
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rcvOrders)
        adapter = ItemTicketAdapter(listTicket, requireActivity(), object : ItemTicketAdapter.IClickListener{
            override fun clickDelete(position: Int) {
            }

            override fun clickNext(ticket: Ticket, schedule: Schedule) {
                val bundle = bundleOf("schedule" to schedule, "ticket" to ticket)
                Log.d("checkdb", "clickNext: " + ticket + " " + schedule)
                val navController = activity?.findNavController(R.id.framelayout)
                val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                bottomNavigationView?.visibility = View.GONE
                navController?.navigate(
                    R.id.action_navigation_orders_to_ordersDefaultFragment,
                    bundle
                )
            }
        })
        binding.rcvOrders.adapter = adapter
    }

    private fun getListTicket() {
        db.collection("users").document(phone).collection("tickets")
            .get()
            .addOnSuccessListener { documentSnapshots ->
                for (document in documentSnapshots) {
                    val ticket = document.toObject(Ticket::class.java)
                    ticket.id = document.id
                    listTicket.add(ticket)
                }
                adapter.notifyDataSetChanged()
            }
    }
}