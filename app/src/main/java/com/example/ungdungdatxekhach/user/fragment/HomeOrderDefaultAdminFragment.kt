package com.example.ungdungdatxekhach.user.fragment

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ListView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.adapter.ItemPopularRouteAdminAdapter
import com.example.ungdungdatxekhach.admin.model.Admin
import com.example.ungdungdatxekhach.databinding.FragmentHomeOrderDefaultAdminBinding
import com.example.ungdungdatxekhach.modelshare.Evaluate
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.example.ungdungdatxekhach.modelshare.adapter.ItemEvaluateAdapter
import com.example.ungdungdatxekhach.user.model.Ticket
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class HomeOrderDefaultAdminFragment : Fragment() {
    private lateinit var binding: FragmentHomeOrderDefaultAdminBinding
    private lateinit var route: Route
    private lateinit var schedule: Schedule
    private lateinit var admin: Admin
    private lateinit var adapter: ItemEvaluateAdapter
    private lateinit var listItem: ArrayList<Evaluate>
    private lateinit var phone: String
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    private val db = Firebase.firestore
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
        if (receivedBundle == null) {
            return
        }
        route = receivedBundle.getSerializable("route") as Route
        schedule = receivedBundle.getSerializable("schedule") as Schedule
        admin = receivedBundle.getSerializable("admin") as Admin

        listItem = ArrayList()

        setInfo()

        val colorClick = ColorStateList.valueOf(android.graphics.Color.parseColor("#00cba9"))
        val colorDilableClick = ColorStateList.valueOf(android.graphics.Color.parseColor("#D2E4E1"))

        binding.tvHomeOrderDefaultInfoBus.setOnClickListener {
            binding.tvHomeOrderDefaultInfoBus.setTextColor(Color.WHITE)
            binding.lnInfoAdmin.visibility = View.VISIBLE
            binding.lnInfoEvaluate.visibility = View.GONE
            binding.tvHomeOrderDefaultEvaluate.setTextColor(Color.BLACK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.tvHomeOrderDefaultInfoBus.backgroundTintList = colorClick
                binding.tvHomeOrderDefaultEvaluate.backgroundTintList = colorDilableClick
            }
            setInfo()
        }
        binding.tvHomeOrderDefaultEvaluate.setOnClickListener {
            getListEvaluate()
            binding.tvHomeOrderDefaultInfoBus.setTextColor(Color.BLACK)
            binding.lnInfoAdmin.visibility = View.GONE
            binding.lnInfoEvaluate.visibility = View.VISIBLE
            binding.tvHomeOrderDefaultEvaluate.setTextColor(Color.WHITE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.tvHomeOrderDefaultInfoBus.backgroundTintList = colorDilableClick
                binding.tvHomeOrderDefaultEvaluate.backgroundTintList = colorClick
            }
            binding.rcvHomeOrderDefaultEvaluate.layoutManager = LinearLayoutManager(requireActivity())
            adapter = ItemEvaluateAdapter(listItem, requireActivity(), object : ItemEvaluateAdapter.IClickListener{
                override fun onClick(position: Int) {

                }

            })
            binding.rcvHomeOrderDefaultEvaluate.adapter = adapter
            binding.rcvHomeOrderDefaultEvaluate.isNestedScrollingEnabled = false

        }
        binding.lnHomeOrderDefaultSchedule.setOnClickListener {
            onClickSchedule()
        }

        binding.imgHomeOrderDefaultBackUser.setOnClickListener {
            onClickBack()
        }
    }

    private fun onClickSchedule() {
        val dialog: Dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_schedule);
        dialog.show();

        val cancle: TextView
        val list: ListView

        cancle = dialog.findViewById(R.id.tvLayoutDialogScheduleCancel)
        list = dialog.findViewById(R.id.lwLayoutDialogSchedule)

        val locationAdapter =
            RouteDefaultBuyTicketStep1.LocationAdapter(requireActivity(), route.location)
        list.adapter = locationAdapter

        cancle.setOnClickListener {
            dialog.dismiss()
        }


        dialog.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow()?.getAttributes()?.windowAnimations = R.style.DialogAnimation
        dialog.getWindow()?.setGravity(Gravity.BOTTOM);
    }


    private fun getListEvaluate() {
        listItem.clear()
        db.collection("evaluates")
            .whereEqualTo("adminId", route.idAdmin)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                for(document in documentSnapshot){
                    var evaluate = document.toObject<Evaluate>()
                    if(evaluate!=null){
                        listItem.add(evaluate)
                    }
                }
                adapter.notifyDataSetChanged()
            }

    }

    private fun setInfo() {
        binding.tvHomeOrderDefaultDepartureTime.text = schedule.dateRoute.pickedHour.toString() + ":"+
                schedule.dateRoute.pickedMinute.toString() + " | " + dateFormat.format(schedule.date)
        binding.tvHomeOrderDefaultSchedule.text = route.departureLocation + " - " + route.destination
        binding.tvHomeOrderDefaultDistance.text = route.distance +" Km"
        binding.tvHomeOrderDefaultPrice.text = route.price + " đ"
        binding.tvHomeOrderDefaultAdminName.text = admin.name
        binding.tvHomeOrderDefaultAdminPhone.text = admin.phone
        binding.tvHomeOrderDefaultAdminEmail.text = admin.email
    }
    private fun onClickBack() {
        val navController = activity?.findNavController(R.id.framelayout)
        navController?.popBackStack()
    }
}