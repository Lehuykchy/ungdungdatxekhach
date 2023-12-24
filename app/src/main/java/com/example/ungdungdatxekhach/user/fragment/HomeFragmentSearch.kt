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
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.adapter.ItemRouteScheduleAdapter
import com.example.ungdungdatxekhach.admin.model.Vehicle
import com.example.ungdungdatxekhach.databinding.FragmentHomeSearchBinding
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class HomeFragmentSearch : Fragment() {
    private lateinit var binding: FragmentHomeSearchBinding
    private lateinit var adapter: ItemRouteScheduleAdapter
    private lateinit var scheduletList: ArrayList<Schedule>
    private lateinit var vehicleList: ArrayList<Vehicle>
    private val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scheduletList = ArrayList()

        val receivedBundle = arguments
        val receivedListObject =
            receivedBundle?.getSerializable("listSchedule") as? ArrayList<Schedule>
        scheduletList = receivedListObject ?: ArrayList()
        binding.rcvHomeSearch.layoutManager = LinearLayoutManager(requireActivity())
        adapter = ItemRouteScheduleAdapter(
            scheduletList,
            requireContext(),
            object : ItemRouteScheduleAdapter.IClickListener {
                override fun clickDelete(id: Int) {

                }

                override fun onClick(position: Int, route: Route) {
                    val receivedIntent = requireActivity().intent
                    val phone = receivedIntent.getStringExtra("phone") ?: ""
                    var schedule = scheduletList.get(position)
                    val bundle =
                        bundleOf("route" to route, "schedule" to schedule, "phone" to phone)
                    val navController = activity?.findNavController(R.id.framelayout)
                    navController?.navigate(
                        R.id.action_homeFragmentSearch_to_routeDefaultBuyTicketStep1,
                        bundle
                    )
                }
            })
        binding.rcvHomeSearch.adapter = adapter
        Log.d("checklist", "onViewCreated: " + scheduletList)
    }
}