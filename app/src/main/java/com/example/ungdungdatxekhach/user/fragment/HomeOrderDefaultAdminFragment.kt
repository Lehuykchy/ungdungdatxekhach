package com.example.ungdungdatxekhach.user.fragment

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.bumptech.glide.Glide
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.Constants
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
import com.google.firebase.storage.FirebaseStorage
import java.text.DecimalFormat
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
        setImage()
        getListEvaluate()

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
        }
        binding.tvHomeOrderDefaultEvaluate.setOnClickListener {

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

    private fun setImage() {
        val storagePath = "images/" + admin.imageBackGroundId //
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child(storagePath)
        Log.d("Firebase Storage", "setImage: " + storageRef)
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            val downloadUrl = uri.toString()
            Glide.with(this)
                .load(downloadUrl)
                .error(R.drawable.baseline_image_24)
                .into(binding.imgHomeOrderDefaultAdmin)
        }.addOnFailureListener { exception ->
            Log.e("Firebase Storage", "Error getting download URL: ${exception.message}")
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
        val decimalFormat = DecimalFormat("#.#")
        db.collection("evaluates")
            .whereEqualTo("adminId", route.idAdmin)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                for (document in documentSnapshot) {
                    var evaluate = document.toObject<Evaluate>()
                    if (evaluate != null) {
                        listItem.add(evaluate)
                    }
                }
                if (listItem.size > 0) {
                    val oneStar = listItem.filter { evaluate -> evaluate.evaluate == 1 }.size
                    val trueStar = listItem.filter { evaluate -> evaluate.evaluate == 2 }.size
                    val threeStar = listItem.filter { evaluate -> evaluate.evaluate == 3 }.size
                    val fourStar = listItem.filter { evaluate -> evaluate.evaluate == 4 }.size
                    val fiveStar = listItem.filter { evaluate -> evaluate.evaluate == 5 }.size

                    binding.tvHomeOrderDefaultAdminStar1.text = oneStar.toString()
                    binding.tvHomeOrderDefaultAdminStar2.text = trueStar.toString()
                    binding.tvHomeOrderDefaultAdminStar3.text = threeStar.toString()
                    binding.tvHomeOrderDefaultAdminStar4.text = fourStar.toString()
                    binding.tvHomeOrderDefaultAdminStar5.text = fiveStar.toString()
                    binding.tvHomeOrderDefaultAdminEvaluate.text = decimalFormat.format(
                        5 * (1 * oneStar + 2 * trueStar + 3 * threeStar + 4 * fourStar + fiveStar * 5) / (5 * listItem.size).toDouble()
                    ).toString() + "/5.0"
                }
            }

    }

    private fun setInfo() {
        binding.tvHomeOrderDefaultDepartureTime.text = schedule.dateRoute.toFormattString() + " | " + dateFormat.format(schedule.date)
        binding.tvHomeOrderDefaultSchedule.text = route.departureLocation + " - " + route.destination
        binding.tvHomeOrderDefaultDistance.text = route.distance +" Km"
        binding.tvHomeOrderDefaultPrice.text = Constants.formatCurrency(route.price.toDouble())
        binding.tvHomeOrderDefaultAdminName.text = admin.name
        binding.tvHomeOrderDefaultAdminPhone.text = admin.phone
        binding.tvHomeOrderDefaultAdminEmail.text = admin.email
    }
    private fun onClickBack() {
        val navController = activity?.findNavController(R.id.framelayout)
        navController?.popBackStack()
    }
}