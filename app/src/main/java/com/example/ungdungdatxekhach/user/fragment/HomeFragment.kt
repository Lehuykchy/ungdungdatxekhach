package com.example.ungdungdatxekhach.user.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungdatxekhach.databinding.FragmentHomeBinding
import com.example.ungdungdatxekhach.user.adapter.ItemPopularRouteAdapter
import com.example.ungdungdatxekhach.user.model.Route

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ItemPopularRouteAdapter
    private lateinit var listItem: ArrayList<Route>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        listItem = ArrayList()
        for (n in 1..10){
            var item : Route = Route("Hà Nội - Tuyên Quang", "100 Km", "100000đ", "03 giờ 00 phút")
            listItem.add(item)
        }
        binding.rcvPopularRouteItem.layoutManager = LinearLayoutManager(activity)
        adapter = ItemPopularRouteAdapter(listItem, requireActivity())
        binding.rcvPopularRouteItem.adapter = adapter
        binding.rcvPopularRouteItem.isNestedScrollingEnabled = false

        return binding.root
    }
}