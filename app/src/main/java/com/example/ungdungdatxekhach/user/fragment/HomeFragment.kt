package com.example.ungdungdatxekhach.user.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.Constants
import com.example.ungdungdatxekhach.admin.adapter.ItemPopularRouteAdminAdapter
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.databinding.FragmentHomeBinding
import com.example.ungdungdatxekhach.modelshare.City
import com.example.ungdungdatxekhach.modelshare.Location
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.example.ungdungdatxekhach.modelshare.TimeRoute
import com.example.ungdungdatxekhach.user.Utils
import com.example.ungdungdatxekhach.user.model.Ticket
import com.example.ungdungdatxekhach.user.model.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.log

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ItemPopularRouteAdminAdapter
    private lateinit var listItem: ArrayList<Route>
    private lateinit var listScheduleSearch: ArrayList<Schedule>
    private val db = Firebase.firestore
    private lateinit var cityList: List<City>
    private var locationDeparture = Location()
    private var locationDestination = Location()
    private var timeRoute = TimeRoute()
    private var countdownTimer: CountDownTimer? = null
    private val COUNTDOWN_TIME = TimeUnit.SECONDS.toMillis(15)
    private var customer = User()
    private var ticket = Ticket()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listItem = ArrayList()
        val first = db.collection("routes").limit(5)

        first.get().addOnSuccessListener { documentSnapshots ->
            for (document in documentSnapshots) {
                val route = document.toObject(Route::class.java)
                route.id = document.id
                listItem.add(route)
            }
            adapter.notifyDataSetChanged()
        }
        binding.rcvPopularRouteItem.layoutManager = LinearLayoutManager(activity)
        adapter = ItemPopularRouteAdminAdapter(
            listItem,
            requireActivity(),
            object : ItemPopularRouteAdminAdapter.OnClickListener {
                override fun onCLick(postion: Int) {
                    val receivedIntent = requireActivity().intent
                    val phone = receivedIntent.getStringExtra("phone") ?: ""
                    var route = listItem.get(postion)
                    val bundle = bundleOf("route" to route, "phone" to phone)
                    val navController = activity?.findNavController(R.id.framelayout)
                    val bottomNavigationView =
                        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                    bottomNavigationView?.visibility = View.GONE
                    navController?.navigate(
                        R.id.action_navigation_home_to_homeRouteDefaultFragment, bundle
                    )
                }

            })
        binding.rcvPopularRouteItem.adapter = adapter
        binding.rcvPopularRouteItem.isNestedScrollingEnabled = false

        binding.lnHomeSelectDeparture.setOnClickListener {
            setLocation { selectedLocation ->
                locationDeparture = selectedLocation
                binding.tvHomeSelectDeparture.text = locationDeparture.other
            }
        }
        binding.lnHomeSelectDestination.setOnClickListener {
            setLocation { selectedLocation ->
                locationDestination = selectedLocation
                binding.tvHomeSelectDestination.text = locationDestination.other
            }
        }
        binding.lnHomeSelectDepartureDate.setOnClickListener {
            setOpenDialogDate()
        }
        binding.btnHomeSearch.setOnClickListener {
            if (ischeck()) {
                listScheduleSearch = ArrayList()
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val dateSearch =
                    dateFormat.parse(binding.tvHomeSelectDepartureDate.text.toString()) ?: Date()

                val routesTask = db.collection("routes").get()

                routesTask.addOnSuccessListener { documentSnapshots ->
                    val scheduleTasks = mutableListOf<Task<Void>>()

                    for (document in documentSnapshots) {
                        val route = document.toObject(Route::class.java)
                        if (route.location.any { it.district == locationDeparture.district } && route.location.any { it.district == locationDestination.district }) {

                            val scheduleTask = db.collection("routes").document(document.id)
                                .collection("schedules").get()
                                .addOnSuccessListener { scheduleDocumentSnapshots ->
                                    for (scheduleDocument in scheduleDocumentSnapshots) {
                                        val schedule =
                                            scheduleDocument.toObject(Schedule::class.java)
                                        schedule.id = scheduleDocument.id
                                        if (dateSearch == schedule.date) {
                                            Log.d(
                                                "checklistSchedule",
                                                "datasearch $dateSearch - $schedule"
                                            )
                                            addListSeacrch(schedule)
                                        }
                                    }
                                }

                            scheduleTasks.add(scheduleTask.continueWith {
                                null
                            })
                        }
                    }

                    Tasks.whenAll(scheduleTasks).addOnSuccessListener {
                        Log.d("checklistHome", "onViewCreated: $listScheduleSearch")

                        val bundle = Bundle().apply {
                            putSerializable("listSchedule", ArrayList(listScheduleSearch))
                        }

                        val navController = activity?.findNavController(R.id.framelayout)
                        val bottomNavigationView =
                            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                        bottomNavigationView?.visibility = View.GONE
                        navController?.navigate(
                            R.id.action_navigation_home_to_homeFragmentSearch, bundle
                        )
                    }
                }
            }
        }

        binding.btnHomeCreate.setOnClickListener {
            onClickBtnCreate()
        }


    }

    private fun onClickBtnCreate() {
        val dialog: Dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_runtime);
        dialog.show();

        val confirm = dialog.findViewById<TextView>(R.id.tvDialogRuntimeConfirmContinue)
        val cancleConfirm = dialog.findViewById<TextView>(R.id.tvDialogRuntimeConfirmCancle)
        val lnConfirm = dialog.findViewById<LinearLayout>(R.id.lnDialogRuntimeConfirm)
        val tvDialogRuntimeConfirmDeparture =
            dialog.findViewById<TextView>(R.id.tvDialogRuntimeConfirmDeparture)
        val tvDialogRuntimeConfirmDestination =
            dialog.findViewById<TextView>(R.id.tvDialogRuntimeConfirmDestination)
        val tvDialogRuntimeConfirmTime =
            dialog.findViewById<TextView>(R.id.tvDialogRuntimeConfirmTime)
        val tvDialogRuntimeConfirmText =
            dialog.findViewById<TextView>(R.id.tvDialogRuntimeConfirmText)

        val lnDialogRuntimeTime = dialog.findViewById<LinearLayout>(R.id.lnDialogRuntimeTime)
        val timePickerDialogRuntime = dialog.findViewById<TimePicker>(R.id.timePickerDialogRuntime)
        val imgDialogRuntimeTimeMinusTicket = dialog.findViewById<ImageView>(R.id.imgDialogRuntimeTimeMinusTicket)
        val tvDialogRuntimeTimeCountTicket = dialog.findViewById<TextView>(R.id.tvDialogRuntimeTimeCountTicket)
        val imgDialogRuntimeTimeAddTicket = dialog.findViewById<ImageView>(R.id.imgDialogRuntimeTimeAddTicket)
        val cancleTime = dialog.findViewById<TextView>(R.id.tvDialogRuntimeTimeDestroy)
        val tvContinue = dialog.findViewById<TextView>(R.id.tvDialogRuntimeTimeContinue)
        timePickerDialogRuntime.is24HourView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timeRoute =
                TimeRoute(
                    timePickerDialogRuntime.hour.toString().toInt(),
                    timePickerDialogRuntime.minute.toString().toInt()
                )
        }

        imgDialogRuntimeTimeMinusTicket.setOnClickListener {
            if (tvDialogRuntimeTimeCountTicket.text.toString().toInt() > 0) {
                val i: Int = tvDialogRuntimeTimeCountTicket.text.toString().toInt() - 1
                tvDialogRuntimeTimeCountTicket.setText(i.toString())
            }
        }

        imgDialogRuntimeTimeAddTicket.setOnClickListener {
            val i: Int = tvDialogRuntimeTimeCountTicket.text.toString().toInt() + 1
            tvDialogRuntimeTimeCountTicket.setText(i.toString())
        }


        tvContinue.setOnClickListener {
            lnConfirm.visibility = View.VISIBLE
            lnDialogRuntimeTime.visibility = View.GONE

            countdownTimer = object : CountDownTimer(COUNTDOWN_TIME, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                    val formattedTime = String.format("%02d", seconds)
                    val countdownText =
                        "Vui lòng xác nhận trong khoảng " + ("\nthời gian  $formattedTime").toString()
                    tvDialogRuntimeConfirmText.text = countdownText
                }

                override fun onFinish() {
                    onClickConfirm(tvDialogRuntimeTimeCountTicket.text.toString().toInt())
                    dialog.dismiss()
                }
            }
            countdownTimer?.start()
        }
        cancleTime.setOnClickListener {
            dialog.dismiss()
        }

        cancleConfirm.setOnClickListener { dialog.dismiss() }
        tvDialogRuntimeConfirmDeparture.text =
            locationDeparture.city + ", " + locationDeparture.district +
                    ", " + locationDeparture.ward + ", " + locationDeparture.other
        tvDialogRuntimeConfirmDestination.text =
            locationDestination.city + ", " + locationDestination.district +
                    ", " + locationDestination.ward + ", " + locationDestination.other
        tvDialogRuntimeConfirmTime.text =
            timeRoute.pickedHour.toString() + ":" + timeRoute.pickedMinute.toString() +
                    " | " + binding.tvHomeSelectDepartureDate.text.toString()

        confirm.setOnClickListener {
            onClickConfirm(tvDialogRuntimeTimeCountTicket.text.toString().toInt())
            dialog.dismiss()
        }

        dialog.getWindow()?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow()?.getAttributes()?.windowAnimations = R.style.DialogAnimation
    }

    private fun onClickConfirm(count: Int) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date =
            dateFormat.parse(binding.tvHomeSelectDepartureDate.text.toString())
                ?: Date()
        val receivedIntent = requireActivity().intent
        var phone = receivedIntent.getStringExtra("phone") ?: ""
        db.collection("users").document(phone)
            .get()
            .addOnSuccessListener { document ->
                customer = document.toObject(User::class.java)!!
                ticket = Ticket(phone, locationDeparture, locationDestination, customer.phone.toString(),
                    customer.email.toString(), customer.name.toString(), count, Constants.STATUS_SEARCH_ADMIN,
                    "", timeRoute,date)
                val bundle = bundleOf(
                    "locationDeparture" to locationDeparture,
                    "locationDestination" to locationDestination,
                    "timeRoute" to timeRoute,
                    "date" to date,
                    "customer" to customer,
                    "ticket" to ticket
                )
                db.collection("tickets")
                    .add(ticket)
                    .addOnSuccessListener { documentReference ->
                        db.collection("users").document(phone).collection("tickets").document(documentReference.id)
                            .set(ticket)
                            .addOnSuccessListener { documentReference ->
                            }
                            .addOnFailureListener { e ->
                            }
                        val navController = activity?.findNavController(R.id.framelayout)
                        val bottomNavigationView =
                            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                        bottomNavigationView?.visibility = View.GONE
                        navController?.navigate(
                            R.id.action_navigation_home_to_homeOrderFragment, bundle
                        )
                    }
                    .addOnFailureListener { e ->
                    }

            }.addOnFailureListener { exception ->
            }

    }


    private fun addListSeacrch(schedule: Schedule) {
        listScheduleSearch.add(schedule)
    }

    private fun setOpenDialogDate() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = requireActivity().let {
            DatePickerDialog(it, { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                binding.tvHomeSelectDepartureDate.text = selectedDate
            }, year, month, day)
        }
        datePickerDialog!!.show()
    }

    private fun setLocation(callback: (Location) -> Unit) {
        val dialog: Dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_location_route);
        dialog.show();

        val ok: Button
        val cancle: Button
        val spinnerDialogCity: Spinner
        val spinnerDialogDistrict: Spinner
        val spinnerDialogWard: Spinner
        val edtDialogOther: EditText

        ok = dialog.findViewById(R.id.btnDialogSave)
        cancle = dialog.findViewById(R.id.btnDialogCancel)
        spinnerDialogCity = dialog.findViewById(R.id.spinnerDialogCity)
        spinnerDialogDistrict = dialog.findViewById(R.id.spinnerDialogDistrict)
        spinnerDialogWard = dialog.findViewById(R.id.spinnerDialogWard)
        edtDialogOther = dialog.findViewById(R.id.edtDialogOther)
        val jsonData = Utils.readJsonFromRawResource(requireActivity(), R.raw.location)
        cityList = Gson().fromJson(jsonData, object : TypeToken<List<City>>() {}.type)
        val cityNames = cityList.map { it.Name }
        val cityAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, cityNames)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDialogCity.adapter = cityAdapter

        spinnerDialogCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long
            ) {
                // Lấy danh sách huyện tương ứng với thành phố được chọn và hiển thị chúng
                val districtNames = cityList[position].districts.map { it.name }
                val districtAdapter = ArrayAdapter(
                    requireActivity(), android.R.layout.simple_spinner_item, districtNames
                )
                districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerDialogDistrict.adapter = districtAdapter

                spinnerDialogDistrict.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parentView: AdapterView<*>?,
                            selectedItemView: View?,
                            position1: Int,
                            id: Long
                        ) {
                            // Lấy danh sách xã tương ứng với huyện được chọn và hiển thị chúng
                            val wardNames =
                                cityList[position].districts[position1].wards.map { it.name }
                            val wardAdapter = ArrayAdapter(
                                requireActivity(), android.R.layout.simple_spinner_item, wardNames
                            )
                            wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinnerDialogWard.adapter = wardAdapter
                        }

                        override fun onNothingSelected(parentView: AdapterView<*>?) {
                            // Không cần xử lý khi không có huyện nào được chọn
                        }
                    }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Không cần xử lý khi không có thành phố nào được chọn
            }
        }




        ok.setOnClickListener {
            if (edtDialogOther.text.isEmpty()) {
                edtDialogOther.error = "Hãy nhập tên"
            } else {
                val selectedLocation = Location(
                    spinnerDialogCity.selectedItem.toString(),
                    spinnerDialogDistrict.selectedItem.toString(),
                    spinnerDialogWard.selectedItem.toString(),
                    edtDialogOther.text.toString()
                )
                callback(selectedLocation)
                dialog.dismiss()
            }
        }

        cancle.setOnClickListener {
            dialog.dismiss()
        }

        dialog.getWindow()?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow()?.getAttributes()?.windowAnimations = R.style.DialogAnimation
        dialog.getWindow()?.setGravity(Gravity.BOTTOM);
    }

    private fun ischeck(): Boolean {
        if (binding.tvHomeSelectDeparture.text.isEmpty()) {
            binding.tvHomeSelectDeparture.error = "Thông tin không được để trống"
            return false
        }
        if (binding.tvHomeSelectDestination.text.isEmpty()) {
            binding.tvHomeSelectDestination.error = "Thông tin không được để trống"
            return false
        }
        if (binding.tvHomeSelectDepartureDate.text.isEmpty()) {
            binding.tvHomeSelectDepartureDate.error = "Thông tin không được để trống"
            return false
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countdownTimer?.cancel()
    }


}
