package com.example.ungdungdatxekhach.user.fragment

import android.app.Dialog
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
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.Constants
import com.example.ungdungdatxekhach.admin.adapter.ItemRouteScheduleAdapter
import com.example.ungdungdatxekhach.admin.adapter.ItemScheduleAdapter
import com.example.ungdungdatxekhach.admin.model.Admin
import com.example.ungdungdatxekhach.admin.model.Vehicle
import com.example.ungdungdatxekhach.databinding.FragmentHomeSearchBinding
import com.example.ungdungdatxekhach.modelshare.ItemCheckBox
import com.example.ungdungdatxekhach.modelshare.Location
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.example.ungdungdatxekhach.modelshare.TimeRoute
import com.example.ungdungdatxekhach.modelshare.adapter.Filter
import com.example.ungdungdatxekhach.modelshare.adapter.ItemBusCheckboxAdapter
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.slider.RangeSlider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class HomeFragmentSearch : Fragment() {
    private lateinit var binding: FragmentHomeSearchBinding
    private lateinit var adapter: ItemScheduleAdapter
    private lateinit var adapterCheckBoxBus: ItemBusCheckboxAdapter
    private lateinit var listCheckboxBus: ArrayList<ItemCheckBox>
    private lateinit var listScheduleFilterOld: ArrayList<Filter>
    private lateinit var listScheduleFilter: ArrayList<Filter>
    private var locationDeparture = Location() // chọn điểm đầu
    private var locationDestination = Location() // chọn điểm cuối
    private var dateSearch = Date() // chọn điểm đầu
    private val db = Firebase.firestore
    private var timeStartFilter = TimeRoute(0, 0)
    private var timeEndFilter = TimeRoute(23, 59)
    private var isFilterSleepCar = false
    private var isFilterCoach = false
    private var filterMaxPrice = 1000000
    private var filterMinPrice = 0
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())


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
//        scheduletList = ArrayList()
        val receivedBundle = arguments
        if (receivedBundle != null) {


            dateSearch = receivedBundle?.getSerializable("dateSearch") as Date
            locationDeparture = receivedBundle?.getSerializable("locationDeparture") as Location
            locationDestination = receivedBundle?.getSerializable("locationDestination") as Location
            Log.d("checkdb", "onViewCreated: " + dateSearch)

            listCheckboxBus = ArrayList()
            listScheduleFilter = ArrayList()
            listScheduleFilterOld = ArrayList()
            getListFilter()

            binding.lnNoData.visibility = View.VISIBLE
            binding.lnHomeSearchFilSort.visibility = View.GONE

            binding.rcvHomeSearch.layoutManager = LinearLayoutManager(activity)
            adapter = ItemScheduleAdapter(
                listScheduleFilter,
                requireActivity(),
                object : ItemScheduleAdapter.IClickListener {
                    override fun clickDelete(id: Int) {
                    }

                    override fun onClick(
                        schedule: Schedule,
                        route: Route,
                        admin: Admin,
                        vehicle: Vehicle
                    ) {
                        val bundle = bundleOf(
                            "route" to route,
                            "schedule" to schedule,
                            "admin" to admin,
                            "vehicle" to vehicle
                        )
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


            binding.lnHomeSearchFilter.setOnClickListener {
                onClickFilter()
            }
            binding.lnHomeSearchSort.setOnClickListener {
                onClickSort()
            }
            binding.imgHomeSearchBack.setOnClickListener {
                onClickSearchBack()
            }
        }
    }

    private fun getListCheckBox() {
        listCheckboxBus.clear()
        val nameIdMap = HashMap<String, String>()

        for (filter in listScheduleFilter) {
            val adminName = filter.admin.name
            val adminId = filter.admin.id

            if (!nameIdMap.containsKey(adminName)) {
                nameIdMap[adminName] = adminId
            }
        }
        for ((name, id) in nameIdMap) {
            val itemCheckBox = ItemCheckBox(id, name, false)
            listCheckboxBus.add(itemCheckBox)
        }
        Log.d("listfilter", "getListCheckBox: " + listCheckboxBus)
    }

    private fun getListFilter() {
        val currentTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
        }
        db.collection("routes").get().addOnSuccessListener { documentSnapshots ->
            for (document in documentSnapshots) {
                val route = document.toObject(Route::class.java)
                route.id = document.id

                if (route.location.any { it.district == locationDeparture.district } &&
                    route.location.any { it.district == locationDestination.district }
                ) {
                    db.collection("routes").document(document.id)
                        .collection("schedules").get()
                        .addOnSuccessListener { scheduleDocumentSnapshots ->
                            for (scheduleDocument in scheduleDocumentSnapshots) {
                                val schedule =
                                    scheduleDocument.toObject(Schedule::class.java)
                                schedule.id = scheduleDocument.id
                                if (dateFormat.format(dateSearch) > dateFormat.format(Date()) && dateFormat.format(dateSearch).equals(dateFormat.format(schedule.date))) {
                                    Log.d(
                                        "checktime",
                                        "getListFilterhomesau: " + combineDateAndTime(
                                            schedule.date,
                                            schedule.dateRoute
                                        )+ "/" + dateFormat.format(dateSearch) + dateFormat.format(Date())
                                    )
                                    db.collection("admins").document(route.idAdmin)
                                        .get()
                                        .addOnSuccessListener { documentAdmin ->
                                            val admin = documentAdmin.toObject<Admin>()
                                            admin?.id = documentAdmin.id

                                            db.collection("admins")
                                                .document(route.idAdmin)
                                                .collection("vehicles")
                                                .document(schedule.vehicleId)
                                                .get()
                                                .addOnSuccessListener { documentVehicle ->
                                                    val vehicle =
                                                        documentVehicle.toObject(
                                                            Vehicle::class.java
                                                        )!!
                                                    vehicle.id = documentVehicle.id
                                                    val filter = Filter(
                                                        schedule,
                                                        admin!!,
                                                        route,
                                                        vehicle
                                                    )
                                                    listScheduleFilter.add(filter)
                                                    listScheduleFilterOld.add(filter)
                                                    Log.d("checklist", "getListFilter: " + filter)
                                                    adapter.notifyDataSetChanged()
                                                    getListCheckBox()
                                                    binding.lnNoData.visibility = View.GONE
                                                    binding.lnHomeSearchFilSort.visibility =
                                                        View.VISIBLE
                                                }

                                        }
                                } else if (dateFormat.format(dateSearch) == dateFormat.format(Date()) && combineDateAndTime(
                                        schedule.date,
                                        schedule.dateRoute
                                    ).after(Date(Date().time ))
                                ) {
                                    Log.d(
                                        "checktime",
                                        "getListFilter: " + combineDateAndTime(
                                            schedule.date,
                                            schedule.dateRoute
                                        )
                                    )
                                    db.collection("admins").document(route.idAdmin)
                                        .get()
                                        .addOnSuccessListener { documentAdmin ->
                                            val admin = documentAdmin.toObject<Admin>()
                                            admin?.id = documentAdmin.id

                                            db.collection("admins")
                                                .document(route.idAdmin)
                                                .collection("vehicles")
                                                .document(schedule.vehicleId)
                                                .get()
                                                .addOnSuccessListener { documentVehicle ->
                                                    val vehicle =
                                                        documentVehicle.toObject(
                                                            Vehicle::class.java
                                                        )!!
                                                    vehicle.id = documentVehicle.id
                                                    val filter = Filter(
                                                        schedule,
                                                        admin!!,
                                                        route,
                                                        vehicle
                                                    )
                                                    listScheduleFilter.add(filter)
                                                    listScheduleFilterOld.add(filter)
                                                    Log.d("checklist", "getListFilter: " + filter)
                                                    adapter.notifyDataSetChanged()
                                                    getListCheckBox()
                                                    binding.lnNoData.visibility = View.GONE
                                                    binding.lnHomeSearchFilSort.visibility =
                                                        View.VISIBLE
                                                }

                                        }


                                }
                            }
                        }
                }
            }


        }


    }

    private fun onClickSearchBack() {
        val navController = activity?.findNavController(R.id.framelayout)
        navController?.popBackStack()
    }

    private fun onClickFilter() {
        val dialog: Dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_filter);
        dialog.show();
        dialog.setCanceledOnTouchOutside(true)

        val lnFilterStart = dialog.findViewById<RelativeLayout>(R.id.lnFilterStart)
        val lnFilterEnd = dialog.findViewById<RelativeLayout>(R.id.lnFilterEnd)
        val imgFilterClose = dialog.findViewById<ImageView>(R.id.imgFilterClose)
        val btnFilterDelete = dialog.findViewById<AppCompatButton>(R.id.btnFilterDelete)
        val btnFilterConfirm = dialog.findViewById<AppCompatButton>(R.id.btnFilterConfirm)
        val rcvFilterBus = dialog.findViewById<RecyclerView>(R.id.rcvFilterBus)
        val rangeSliderFilterPrice = dialog.findViewById<RangeSlider>(R.id.rangeSliderFilterPrice)
        val tvFilterMinPrice = dialog.findViewById<TextView>(R.id.tvFilterMinPrice)
        val tvFilterMaxPrice = dialog.findViewById<TextView>(R.id.tvFilterMaxPrice)
        val rltFilterCoach = dialog.findViewById<RelativeLayout>(R.id.rltFilterCoach)
        val cbFilterCoach = dialog.findViewById<CheckBox>(R.id.cbFilterCoach)
        val rltFilterSleepCar = dialog.findViewById<RelativeLayout>(R.id.rltFilterSleepCar)
        val cbFilterSleepCar = dialog.findViewById<CheckBox>(R.id.cbFilterSleepCar)
        val tvFilterStartTime = dialog.findViewById<TextView>(R.id.tvFilterStartTime)
        val tvFilterEndTime = dialog.findViewById<TextView>(R.id.tvFilterEndTime)

        tvFilterStartTime.text = timeStartFilter.toFormattString()
        tvFilterEndTime.text = timeEndFilter.toFormattString()
        cbFilterCoach.isChecked = isFilterCoach
        cbFilterSleepCar.isChecked = isFilterSleepCar
        tvFilterMinPrice.text = Constants.formatCurrency(filterMinPrice.toDouble())
        tvFilterMaxPrice.text = Constants.formatCurrency(filterMaxPrice.toDouble())

        var initialRangeSliderValues: FloatArray =
            floatArrayOf(filterMinPrice.toFloat(), filterMaxPrice.toFloat())
        rangeSliderFilterPrice.values =
            initialRangeSliderValues.toList().toTypedArray().toMutableList()

        rcvFilterBus.layoutManager = LinearLayoutManager(requireActivity())
        adapterCheckBoxBus = ItemBusCheckboxAdapter(
            listCheckboxBus,
            requireActivity(),
            object : ItemBusCheckboxAdapter.IClickListener {
                override fun onClick(position: Int, ischecked: Boolean) {
                    listCheckboxBus.get(position).ischeck = ischecked
                    adapterCheckBoxBus.notifyDataSetChanged()
                    Log.d("checkfilterdata", "onClickFilterCheckbox: " + listCheckboxBus)
                }

            })
        rcvFilterBus.adapter = adapterCheckBoxBus

        lnFilterStart.setOnClickListener {
            onClickTime(tvFilterStartTime, tvFilterEndTime)
        }

        lnFilterEnd.setOnClickListener {
            onClickTime(tvFilterStartTime, tvFilterEndTime)
        }

        rltFilterCoach.setOnClickListener {
            if (cbFilterCoach.isChecked) {
                cbFilterCoach.isChecked = false
                isFilterCoach = false
            } else {
                cbFilterCoach.isChecked = true
                isFilterCoach = true
            }
        }
        rltFilterSleepCar.setOnClickListener {
            if (cbFilterSleepCar.isChecked) {
                cbFilterSleepCar.isChecked = false
                isFilterSleepCar = false
            } else {
                cbFilterSleepCar.isChecked = true
                isFilterSleepCar = true
            }
        }
        rangeSliderFilterPrice.addOnChangeListener { slider, _, _ ->
            val selectedMinValue = rangeSliderFilterPrice.values[0]
            val selectedMaxValue = rangeSliderFilterPrice.values[1]

            filterMinPrice = rangeSliderFilterPrice.values[0].toInt()
            filterMaxPrice = rangeSliderFilterPrice.values[1].toInt()

            tvFilterMinPrice.text = selectedMinValue.toString()
            tvFilterMaxPrice.text = selectedMaxValue.toString()
        }

        imgFilterClose.setOnClickListener {
            dialog.dismiss()
        }


        btnFilterConfirm.setOnClickListener {
            var araylist = ArrayList<Filter>()
            araylist.addAll(listScheduleFilterOld)
            var filterTime = araylist.filter { filter ->
                val scheduleTime = filter.schedule.dateRoute.toFormattedString()
                scheduleTime != null && scheduleTime in timeStartFilter.toFormattedString()..timeEndFilter.toFormattedString()
            }
            filterTime =
                ArrayList(filterTime.sortedBy { it.schedule.dateRoute.toFormattedString() })
            for (filter in filterTime) {
                Log.d(
                    "checkfilterdata",
                    "onClickFilterTime: " + filter.schedule.dateRoute.toFormattedString()
                )
            }
            val filterPrice = filterTime.filter { filter ->
                val schedulePrice = filter.route.price.toFloat()
                schedulePrice != null && schedulePrice in rangeSliderFilterPrice.values[0]..rangeSliderFilterPrice.values[1]
            }
            var filterList = ArrayList<Filter>()
            for (filter in filterPrice) {
                Log.d("checkfilterdata", "onClickFilterPrice: " + filter.route.price)
            }
            if ((cbFilterCoach.isChecked && cbFilterSleepCar.isChecked) || ((!cbFilterCoach.isChecked && !cbFilterSleepCar.isChecked))) {
                filterList = filterPrice as ArrayList<Filter>
            } else {
                if (cbFilterCoach.isChecked) {
                    for (filter in filterPrice) {
                        if (filter.vehicle.type.equals("Xe khách")) {
                            filterList.add(filter)
                        }
                    }
                } else if (cbFilterSleepCar.isChecked) {
                    for (filter in filterPrice) {
                        if (filter.vehicle.type.equals("Xe giường nằm")) {
                            filterList.add(filter)
                        }
                    }
                }
            }
            for (filter in filterList) {
                Log.d("checkfilterdata", "onClickFilterPrice: " + filter.vehicle.type)
            }

            var count = 0
            for (checkboxBus in listCheckboxBus) {
                if (checkboxBus.ischeck) {
                    count++
                }
            }
            if (count == 0) {
                adapter.setData(filterList)
            } else {
                var filterBus = ArrayList<Filter>()
                for (checkboxBus in listCheckboxBus) {
                    if (checkboxBus.ischeck) {
                        for (filter in filterPrice) {
                            if (filter.admin.id.equals(checkboxBus.id)) {
                                filterBus.add(filter)
                            }
                        }
                    }
                }
                adapter.setData(filterBus)
            }
            dialog.dismiss()
        }

        btnFilterDelete.setOnClickListener {
            timeStartFilter = TimeRoute(0, 0)
            timeEndFilter = TimeRoute(23, 59)
            isFilterSleepCar = false
            isFilterCoach = false
            filterMaxPrice = 1000000
            filterMinPrice = 0
            tvFilterStartTime.text = timeStartFilter.toFormattString()
            tvFilterEndTime.text = timeEndFilter.toFormattString()
            cbFilterCoach.isChecked = isFilterCoach
            cbFilterSleepCar.isChecked = isFilterSleepCar
            tvFilterMinPrice.text = Constants.formatCurrency(filterMinPrice.toDouble())
            tvFilterMaxPrice.text = Constants.formatCurrency(filterMaxPrice.toDouble())

            var initialRangeSliderValues: FloatArray =
                floatArrayOf(filterMinPrice.toFloat(), filterMaxPrice.toFloat())
            rangeSliderFilterPrice.values =
                initialRangeSliderValues.toList().toTypedArray().toMutableList()
            for (listCheck in listCheckboxBus) {
                listCheck.ischeck = false
                adapterCheckBoxBus.notifyDataSetChanged()
            }
        }



        dialog.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow()?.getAttributes()?.windowAnimations = R.style.DialogAnimation
        dialog.getWindow()?.setGravity(Gravity.BOTTOM);
    }

    private fun onClickTime(tvStart: TextView, tvEnd: TextView) {
        val dialog: Dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_time);
        dialog.show();
        dialog.setCanceledOnTouchOutside(true)

        val imgTimeClose = dialog.findViewById<ImageView>(R.id.imgTimeClose)
        val timeTimeEnd = dialog.findViewById<TimePicker>(R.id.timeTimeEnd)
        val timeTimeStart = dialog.findViewById<TimePicker>(R.id.timeTimeStart)
        val btnConfirm = dialog.findViewById<AppCompatButton>(R.id.btnTimeConfirm)
        timeTimeStart.is24HourView
        timeTimeEnd.is24HourView

        imgTimeClose.setOnClickListener {
            dialog.dismiss()
        }
        btnConfirm.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timeStartFilter.pickedHour = timeTimeStart.hour
                timeStartFilter.pickedMinute = timeTimeStart.minute
                timeEndFilter.pickedHour = timeTimeEnd.hour
                timeEndFilter.pickedMinute = timeTimeEnd.minute
                tvStart.text =
                    timeStartFilter.pickedHour.toString() + ":" + timeStartFilter.pickedMinute.toString()
                tvEnd.text =
                    timeEndFilter.pickedHour.toString() + ":" + timeEndFilter.pickedMinute.toString()
            }
            dialog.dismiss()
        }

        dialog.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow()?.getAttributes()?.windowAnimations = R.style.DialogAnimation
        dialog.getWindow()?.setGravity(Gravity.BOTTOM);
    }

    private fun onClickSort() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_dialog_sort)
        dialog.show()
        dialog.setCanceledOnTouchOutside(true)

        val imgSortClose = dialog.findViewById<ImageView>(R.id.imgSortClose)
        val tvSortIncrease = dialog.findViewById<TextView>(R.id.tvSortIncrease)
        val tvSortReduce = dialog.findViewById<TextView>(R.id.tvSortReduce)
        val tvSortTime = dialog.findViewById<TextView>(R.id.tvSortTime)

        imgSortClose.setOnClickListener {
            dialog.dismiss()
        }
        tvSortIncrease.setOnClickListener {
            onClickSortIncrease()
            listScheduleFilter = adapter.getFilter()
            dialog.dismiss()
        }
        tvSortReduce.setOnClickListener {
            onClickSortReduce()
            dialog.dismiss()
        }
        tvSortTime.setOnClickListener {
            onClickSortTime()
            dialog.dismiss()
        }
        dialog.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow()?.getAttributes()?.windowAnimations = R.style.DialogAnimation
        dialog.getWindow()?.setGravity(Gravity.BOTTOM);

    }

    private fun onClickSortTime() {
        adapter.setData(ArrayList(listScheduleFilter.sortedBy { it.schedule.dateRoute.toFormattedString() }))
    }

    private fun onClickSortReduce() {
        adapter.setData(ArrayList(listScheduleFilter.sortedByDescending { it.route.price }))
    }

    private fun onClickSortIncrease() {
        adapter.setData(ArrayList(listScheduleFilter.sortedBy { it.route.price }))

    }


    fun combineDateAndTime(date: Date, timeRoute: TimeRoute): Date {
        val combinedCalendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, timeRoute.pickedHour)
            set(Calendar.MINUTE, timeRoute.pickedMinute)
        }

        return combinedCalendar.time
    }


}