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
import com.example.ungdungdatxekhach.admin.adapter.ItemRouteScheduleAdapter
import com.example.ungdungdatxekhach.admin.adapter.ItemScheduleAdapter
import com.example.ungdungdatxekhach.admin.model.Admin
import com.example.ungdungdatxekhach.databinding.FragmentHomeSearchBinding
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragmentSearch : Fragment() {
    private lateinit var binding: FragmentHomeSearchBinding
    private lateinit var adapter: ItemScheduleAdapter
    private lateinit var scheduletList: ArrayList<Schedule>
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
        if(scheduletList.size == 0 || scheduletList.size == null){
            binding.lnNoData.visibility = View.VISIBLE
        }else{
            binding.lnNoData.visibility = View.GONE
        }
        binding.rcvHomeSearch.layoutManager = LinearLayoutManager(activity)
        adapter = ItemScheduleAdapter(
            scheduletList,
            requireActivity(),
            object : ItemScheduleAdapter.IClickListener {
                override fun clickDelete(id: Int) {
                }

                override fun onClick(position: Int, route: Route, admin: Admin) {
                    val bundle = bundleOf("route" to route, "schedule" to scheduletList.get(position), "admin" to admin)
                    val navController = activity?.findNavController(R.id.framelayout)
                    val bottomNavigationView =
                        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                    bottomNavigationView?.visibility = View.GONE
                    navController?.navigate(
                        R.id.action_homeFragmentSearch_to_routeDefaultBuyTicketStep1, bundle
                    )
                }

            })
        binding.rcvHomeSearch.adapter = adapter
        binding.imgHomeSearchBack.setOnClickListener {
            onClickSearchBack()
        }
    }

    private fun onClickSearchBack() {
        val navController = activity?.findNavController(R.id.framelayout)
        navController?.popBackStack()
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView?.visibility = View.VISIBLE
    }
}