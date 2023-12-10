package com.example.ungdungdatxekhach.admin.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.adapter.ItemPopularRouteAdminAdapter
import com.example.ungdungdatxekhach.admin.model.Route
import com.example.ungdungdatxekhach.databinding.AdminFragmentRoutesBinding
import com.example.ungdungdatxekhach.user.adapter.ItemPopularRouteAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase


class RoutesFragment : Fragment() {
    private lateinit var binding: AdminFragmentRoutesBinding
    private lateinit var listItem: ArrayList<Route>
    private lateinit var adapter: ItemPopularRouteAdminAdapter
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AdminFragmentRoutesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lnAddRoute.setOnClickListener { onClickAddRoute() }
        listItem = ArrayList()
        getListItem()
        binding.rcvRoutes.layoutManager = LinearLayoutManager(activity)
        adapter = ItemPopularRouteAdminAdapter(listItem, requireActivity())
        binding.rcvRoutes.adapter = adapter
        binding.rcvRoutes.isNestedScrollingEnabled = false
    }

    private fun getListItem() {
        val user = Firebase.auth.currentUser
        var id: String = ""
        user?.let {
            id = it.uid.toString()
        }
        db.collection("admins")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val route = document.toObject<Route>()
                    if (route != null) {
                        listItem.add(route)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
            }
    }

    private fun onClickAddRoute() {
        val navController = activity?.findNavController(R.id.framelayoutAdmin)
        navController?.navigate(R.id.action_navigation_routes_admin_to_routeAddFragment2)
    }
}