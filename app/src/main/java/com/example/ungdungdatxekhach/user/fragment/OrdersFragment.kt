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
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.Constants
import com.example.ungdungdatxekhach.databinding.FragmentOrdersBinding
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.example.ungdungdatxekhach.user.adapter.ItemTicketAdapter
import com.example.ungdungdatxekhach.user.model.Ticket
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.checkerframework.checker.units.qual.A
import org.checkerframework.checker.units.qual.C

class OrdersFragment : Fragment() {
    private lateinit var binding: FragmentOrdersBinding
    private lateinit var listTicket: ArrayList<Ticket>
    private lateinit var listTicketFilter: ArrayList<Ticket>
    private lateinit var adapter: ItemTicketAdapter
    private val db = Firebase.firestore
    private lateinit var phone:String
    private val myBooleanArray = BooleanArray(7) { false }
    private var listStatus: Set<String> = setOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        val bottomNavigationView =
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView?.visibility = View.VISIBLE
        for (i in myBooleanArray.indices) {
            myBooleanArray[i] = false
        }
        listStatus = setOf()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val i = requireActivity().intent
        phone = i.getStringExtra("phone").toString()
        listTicket = ArrayList()
        listTicketFilter = ArrayList()
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

            override fun clickNextOrder(ticket: Ticket) {
                val bundle = bundleOf(
                    "ticket" to ticket
                )
                val navController = activity?.findNavController(R.id.framelayout)
                val bottomNavigationView =
                    activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                bottomNavigationView?.visibility = View.GONE
                navController?.navigate(
                    R.id.action_navigation_orders_to_homeOrderFragment, bundle
                )
            }
        })
        binding.rcvOrders.adapter = adapter
        binding.rcvOrders.isNestedScrollingEnabled = false
        binding.lnOrderFilter.setOnClickListener {
            setOnClickFilter()
        }
    }

    private fun setOnClickFilter() {
        val dialog: Dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_filter_order);
        dialog.show();
        dialog.setCanceledOnTouchOutside(true)

        val rltWaitPaid = dialog.findViewById<RelativeLayout>(R.id.rltWaitPaid)
        val cbWaitPaid = dialog.findViewById<CheckBox>(R.id.cbWaitPaid)
        val rltPaid = dialog.findViewById<RelativeLayout>(R.id.rltPaid)
        val cbPaid = dialog.findViewById<CheckBox>(R.id.cbPaid)
        val rltTimeOut = dialog.findViewById<RelativeLayout>(R.id.rltTimeOut)
        val cbTimeOut = dialog.findViewById<CheckBox>(R.id.cbTimeOut)
        val rltSuccess = dialog.findViewById<RelativeLayout>(R.id.rltSuccess)
        val cbSuccess = dialog.findViewById<CheckBox>(R.id.cbSuccess)
        val rltEvaluate = dialog.findViewById<RelativeLayout>(R.id.rltEvaluate)
        val cbEvaluate = dialog.findViewById<CheckBox>(R.id.cbEvaluate)
        val rltSearchBus = dialog.findViewById<RelativeLayout>(R.id.rltSearchBus)
        val cbSearchBus = dialog.findViewById<CheckBox>(R.id.cbSearchBus)
        val rltDestroy = dialog.findViewById<RelativeLayout>(R.id.rltDestroy)
        val cbDestroy = dialog.findViewById<CheckBox>(R.id.cbDestroy)
        val imgFilterClose = dialog.findViewById<ImageView>(R.id.imgFilterClose)
        val btnDeleleFilter = dialog.findViewById<AppCompatButton>(R.id.btnOrderDelete)
        val btnOrderConfirm = dialog.findViewById<AppCompatButton>(R.id.btnOrderConfirm)

        cbWaitPaid.isChecked = myBooleanArray[0]
        cbPaid.isChecked = myBooleanArray[1]
        cbTimeOut.isChecked = myBooleanArray[2]
        cbSuccess.isChecked = myBooleanArray[3]
        cbEvaluate.isChecked = myBooleanArray[4]
        cbSearchBus.isChecked = myBooleanArray[5]
        cbDestroy.isChecked = myBooleanArray[6]



        rltWaitPaid.setOnClickListener {
            cbWaitPaid.isChecked = !cbWaitPaid.isChecked
            myBooleanArray[0] = cbWaitPaid.isChecked
            if(cbWaitPaid.isChecked){
                listStatus += Constants.STATUS_WAIT_PAID
            }else{
                listStatus -= Constants.STATUS_WAIT_PAID
            }
        }
        rltPaid.setOnClickListener {
            cbPaid.isChecked = !cbPaid.isChecked
            myBooleanArray[1] = cbPaid.isChecked
            if(cbPaid.isChecked){
                listStatus += Constants.STATUS_PAID
            }else{
                listStatus -= Constants.STATUS_PAID
            }
        }
        rltTimeOut.setOnClickListener {
            cbTimeOut.isChecked = !cbTimeOut.isChecked
            myBooleanArray[2] = cbTimeOut.isChecked
            if(cbTimeOut.isChecked){
                listStatus += Constants.STATUS_TIMEOUT
            }else{
                listStatus -= Constants.STATUS_TIMEOUT
            }
        }
        rltSuccess.setOnClickListener {
            cbSuccess.isChecked = !cbSuccess.isChecked
            myBooleanArray[3] = cbSuccess.isChecked
            if(cbSuccess.isChecked){
                listStatus += Constants.STATUS_SUCCESS
            }else{
                listStatus -= Constants.STATUS_SUCCESS
            }
        }
        rltEvaluate.setOnClickListener {
            cbEvaluate.isChecked = !cbEvaluate.isChecked
            myBooleanArray[4] = cbEvaluate.isChecked
            if(cbEvaluate.isChecked){
                listStatus += Constants.STATUS_EVALUATE
            }else{
                listStatus -= Constants.STATUS_EVALUATE
            }
        }
        rltSearchBus.setOnClickListener {
            cbSearchBus.isChecked = !cbSearchBus.isChecked
            myBooleanArray[5] = cbSearchBus.isChecked
            if(cbSearchBus.isChecked){
                listStatus += Constants.STATUS_SEARCH_ADMIN
            }else{
                listStatus -= Constants.STATUS_SEARCH_ADMIN
            }
        }
        rltDestroy.setOnClickListener {
            cbDestroy.isChecked = !cbDestroy.isChecked
            myBooleanArray[6] = cbDestroy.isChecked
            if(cbDestroy.isChecked){
                listStatus += Constants.STATUS_DESTROY
            }else{
                listStatus -= Constants.STATUS_DESTROY
            }
        }

        btnOrderConfirm.setOnClickListener {
            var mylist = ArrayList<Ticket>()
            mylist.addAll(listTicketFilter)
            if (listStatus.isEmpty()) {
                adapter.setListData(mylist)
            } else {
                val filteredList = mylist.filter { ticket ->
                    ticket.status in listStatus
                }
                adapter.setListData(filteredList as ArrayList<Ticket>)
            }
            Log.d("checkorder", "setOnClickFilter: " + myBooleanArray[0]+ myBooleanArray[1]+
                    myBooleanArray[2]+ myBooleanArray[3]+ myBooleanArray[4]+ myBooleanArray[5])
            Log.d("checkorder", "setOnClickFilter: " + listStatus)
            dialog.dismiss()
        }

        imgFilterClose.setOnClickListener {
            dialog.dismiss()
        }
        btnDeleleFilter.setOnClickListener {
            cbWaitPaid.isChecked = false
            cbPaid.isChecked = false
            cbEvaluate.isChecked = false
            cbDestroy.isChecked = false
            cbSuccess.isChecked = false
            cbSearchBus.isChecked = false
            cbTimeOut.isChecked = false
            for (i in myBooleanArray.indices) {
                myBooleanArray[i] = false
            }
            listStatus = setOf()
        }


        dialog.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow()?.getAttributes()?.windowAnimations = R.style.DialogAnimation
        dialog.getWindow()?.setGravity(Gravity.BOTTOM);
    }

    private fun getListTicket() {
        db.collection("users").document(phone).collection("tickets")
            .orderBy("dateDeparture", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documentSnapshots ->
                for (document in documentSnapshots) {
                    val ticket = document.toObject(Ticket::class.java)
                    ticket.id = document.id
                    listTicket.add(ticket)
                    listTicketFilter.add(ticket)
                }
                adapter.notifyDataSetChanged()
                if(listTicket.size>0){
                    binding.lnOrderFilter.visibility = View.VISIBLE
                    binding.lnNoData.visibility = View.GONE
                }else{
                    binding.lnOrderFilter.visibility = View.GONE
                    binding.lnNoData.visibility = View.VISIBLE
                }
            }
    }
}