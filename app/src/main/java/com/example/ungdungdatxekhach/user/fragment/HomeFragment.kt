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
import com.example.ungdungdatxekhach.admin.adapter.ItemPopularRouteAdminAdapter
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ItemPopularRouteAdminAdapter
    private lateinit var listItem: ArrayList<Route>
    private val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        listItem = ArrayList()
        val first = db.collection("routes")
            .limit(5)

        first.get()
            .addOnSuccessListener { documentSnapshots ->
                for (document in documentSnapshots) {
                    val route = document.toObject(Route::class.java)
                    route.id = document.id
                    listItem.add(route)
                }
                adapter.notifyDataSetChanged()
            }
        binding.rcvPopularRouteItem.layoutManager = LinearLayoutManager(activity)
        adapter = ItemPopularRouteAdminAdapter(listItem, requireActivity(), object : ItemPopularRouteAdminAdapter.OnClickListener{
            override fun onCLick(postion: Int) {
                val receivedIntent = requireActivity().intent
                val phone = receivedIntent.getStringExtra("phone") ?: ""
                var route = listItem.get(postion)
                val bundle = bundleOf("route" to route, "phone" to phone)
                val navController = activity?.findNavController(R.id.framelayout)
                val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                bottomNavigationView?.visibility = View.GONE
                navController?.navigate(R.id.action_navigation_home_to_homeRouteDefaultFragment, bundle)
            }

        })
        binding.rcvPopularRouteItem.adapter = adapter
        binding.rcvPopularRouteItem.isNestedScrollingEnabled = false

        return binding.root
    }
}