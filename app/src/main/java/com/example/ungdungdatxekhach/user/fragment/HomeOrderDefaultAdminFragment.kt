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
import com.example.ungdungdatxekhach.admin.model.Admin
import com.example.ungdungdatxekhach.databinding.FragmentHomeOrderDefaultAdminBinding
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.user.model.Ticket
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class HomeOrderDefaultAdminFragment : Fragment() {
    private lateinit var binding: FragmentHomeOrderDefaultAdminBinding
    private var ticket = Ticket()
    private var admin = Admin()
    private lateinit var listRoute : ArrayList<Route>
    private val db = Firebase.firestore
    private lateinit var adapter: ItemPopularRouteAdminAdapter
    private var checkInfo = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeOrderDefaultAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val receivedBundle = arguments
        if ((receivedBundle == null)) {
            return
        }
        ticket = receivedBundle.getSerializable("ticket") as Ticket
        listRoute = ArrayList()
        getAdmin()
        getListRoute()
        binding.imgHomeOrderBack.setOnClickListener {
            onClickBack()
        }
        binding.rcvHomeOrderDefaultAdmin.layoutManager = LinearLayoutManager(requireActivity())
        adapter = ItemPopularRouteAdminAdapter(
            listRoute,
            requireActivity(),
            object : ItemPopularRouteAdminAdapter.OnClickListener {
                override fun onCLick(postion: Int) {
                    val phone = ticket.phone
                    var route = listRoute.get(postion)
                    val bundle = bundleOf("route" to route, "phone" to phone)
                    val navController = activity?.findNavController(R.id.framelayout)
                    navController?.navigate(
                        R.id.action_homeOrderDefaultAdminFragment_to_homeRouteDefaultFragment, bundle
                    )
                }

            })
        binding.rcvHomeOrderDefaultAdmin.adapter = adapter
        binding.lnClickInfoAdmin.setOnClickListener {
            if(checkInfo){
                binding.lnInfoAdmin.visibility = View.GONE
                checkInfo =false
            }else{
                binding.lnInfoAdmin.visibility = View.VISIBLE
                checkInfo = true
            }
        }


    }

    private fun getAdmin() {
        db.collection("admins").document(ticket.adminId)
            .get()
            .addOnSuccessListener { document ->
                admin = document.toObject(Admin::class.java)!!
                binding.tvHomeOrderDefaultAdminName.text = admin.name
                binding.tvHomeOrderDefaultAdminPhone.text = admin.phone
                binding.tvHomeOrderDefaultAdminEmail.text = admin.email
                if (!admin.description.isEmpty()) {
                    binding.tvHomeOrderDefaultAdminInfomation.text = admin.description
                }
            }
    }

    private fun getListRoute() {
        db.collection("routes")
            .whereEqualTo("idAdmin", ticket.adminId.toString())
            .get()
            .addOnSuccessListener { documentSnapshots ->
                for (document in documentSnapshots) {
                    val route = document.toObject(Route::class.java)
                    route.id = document.id
                    listRoute.add(route)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception -> }
    }

    private fun onClickBack() {
        val navController = activity?.findNavController(R.id.framelayout)
        navController?.popBackStack()
    }
}