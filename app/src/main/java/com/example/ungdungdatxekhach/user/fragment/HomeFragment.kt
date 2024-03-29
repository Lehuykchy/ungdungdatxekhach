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
import com.bumptech.glide.Glide
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.Constants
import com.example.ungdungdatxekhach.admin.adapter.ItemPopularRouteAdminAdapter
import com.example.ungdungdatxekhach.admin.adapter.ItemScheduleAdapter
import com.example.ungdungdatxekhach.admin.model.Admin
import com.example.ungdungdatxekhach.admin.model.Vehicle
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.databinding.FragmentHomeBinding
import com.example.ungdungdatxekhach.modelshare.City
import com.example.ungdungdatxekhach.modelshare.Location
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.example.ungdungdatxekhach.modelshare.TimeRoute
import com.example.ungdungdatxekhach.modelshare.adapter.Filter
import com.example.ungdungdatxekhach.user.Utils
import com.example.ungdungdatxekhach.user.model.Ticket
import com.example.ungdungdatxekhach.user.model.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.log

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    //    private lateinit var adapter: ItemScheduleAdapter
    private lateinit var listItem: ArrayList<Schedule>
    private lateinit var listScheduleSearch: ArrayList<Schedule>
    private val db = Firebase.firestore
    private lateinit var cityList: List<City> // chọn spinner
    private var locationDeparture = Location() // chọn điểm đầu
    private var locationDestination = Location() // chọn điểm cuối
    private var timeRoute = TimeRoute() // chọn thời gian
    private var countdownTimer: CountDownTimer? = null
    private val COUNTDOWN_TIME = TimeUnit.SECONDS.toMillis(15)
    private var customer = User()
    private var ticket = Ticket()
    private val formatDate = SimpleDateFormat("dd/MM/yyyy")
    private lateinit var listFilter: ArrayList<Filter>
    private lateinit var user: User
    private lateinit var storageReference: StorageReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val bottomNavigationView =
            activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView?.visibility = View.VISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCustomer()
        binding.tvHomeSelectDepartureDate.text = formatDate.format(Date())
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
                listFilter = ArrayList()
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val dateSearch =
                    dateFormat.parse(binding.tvHomeSelectDepartureDate.text.toString()) ?: Date()
                val bundle = bundleOf(
                    "locationDeparture" to locationDeparture,
                    "locationDestination" to locationDestination,
                    "dateSearch" to dateSearch
                )

                val navController = activity?.findNavController(R.id.framelayout)
                val bottomNavigationView =
                    activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                bottomNavigationView?.visibility = View.GONE
                navController?.navigate(
                    R.id.action_navigation_home_to_homeFragmentSearch,
                    bundle
                )

            }
        }



        binding.btnHomeCreate.setOnClickListener {
            if (ischeck()) {
                onClickBtnCreate()
            }
        }

        binding.cardviewHomeContact.setOnClickListener {
            val navController = activity?.findNavController(R.id.framelayout)
            val bottomNavigationView =
                activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNavigationView?.visibility = View.GONE
            navController?.navigate(R.id.action_navigation_home_to_profileEditFragment)
        }


    }

    private fun getCustomer() {
        val i = requireActivity().intent
        var phone = i.getStringExtra("phone").toString()
        try {
            db.collection("users").document(phone)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        user = document.toObject<User>()!!
                        val storagePath = "images/" + user.imageId //
                        val storage = FirebaseStorage.getInstance()
                        val storageRef = storage.reference.child(storagePath)
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            val downloadUrl = uri.toString()
                            Glide.with(this)
                                .load(downloadUrl)
                                .into(binding.imgEditcontact)
                        }.addOnFailureListener { exception ->
                            Log.e(
                                "Firebase Storage",
                                "Error getting download URL: ${exception.message}"
                            )
                        }
                    }
                }
                .addOnFailureListener { exception ->
                }
        } catch (e: ParseException) {
            e.printStackTrace()
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
        val imgDialogRuntimeTimeMinusTicket =
            dialog.findViewById<ImageView>(R.id.imgDialogRuntimeTimeMinusTicket)
        val tvDialogRuntimeTimeCountTicket =
            dialog.findViewById<TextView>(R.id.tvDialogRuntimeTimeCountTicket)
        val imgDialogRuntimeTimeAddTicket =
            dialog.findViewById<ImageView>(R.id.imgDialogRuntimeTimeAddTicket)
        val cancleTime = dialog.findViewById<TextView>(R.id.tvDialogRuntimeTimeDestroy)
        val tvContinue = dialog.findViewById<TextView>(R.id.tvDialogRuntimeTimeContinue)
        timePickerDialogRuntime.is24HourView


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
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            if(tvDialogRuntimeTimeCountTicket.text.toString().toInt()==0){
                Toast.makeText(requireActivity(), "Hãy chọn số vé", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val time = timePickerDialogRuntime.hour.toString()
                    .toInt() * 60 + timePickerDialogRuntime.minute.toString()
                    .toInt() - hour * 60 - minute
                timeRoute =
                    TimeRoute(
                        timePickerDialogRuntime.hour.toString().toInt(),
                        timePickerDialogRuntime.minute.toString().toInt()
                    )


                if (formatDate.format(Date()).equals(binding.tvHomeSelectDepartureDate.text)) {
                    if (time >= 15) {
                        lnConfirm.visibility = View.VISIBLE
                        lnDialogRuntimeTime.visibility = View.GONE
                        tvDialogRuntimeConfirmDeparture.text =
                            locationDeparture.city + ", " + locationDeparture.district +
                                    ", " + locationDeparture.ward + ", " + locationDeparture.other
                        tvDialogRuntimeConfirmDestination.text =
                            locationDestination.city + ", " + locationDestination.district +
                                    ", " + locationDestination.ward + ", " + locationDestination.other
                        tvDialogRuntimeConfirmTime.text =
                            timeRoute.pickedHour.toString() + ":" + timeRoute.pickedMinute.toString() +
                                    " | " + binding.tvHomeSelectDepartureDate.text.toString()
                        countdownTimer = object : CountDownTimer(COUNTDOWN_TIME, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                                val formattedTime = String.format("%02d", seconds)
                                val countdownText =
                                    "Vui lòng xác nhận trong khoảng " + ("\nthời gian  $formattedTime").toString()
                                tvDialogRuntimeConfirmText.text = countdownText
                            }

                            override fun onFinish() {
                                onClickConfirm(
                                    tvDialogRuntimeTimeCountTicket.text.toString().toInt()
                                )
                                dialog.dismiss()
                            }
                        }
                        countdownTimer?.start()
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            "Vui lòng chọn thời gian xuất phát lớn hơn thời gian hiện tại 15 phút!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {

                    lnConfirm.visibility = View.VISIBLE
                    lnDialogRuntimeTime.visibility = View.GONE
                    tvDialogRuntimeConfirmDeparture.text =
                        locationDeparture.city + ", " + locationDeparture.district +
                                ", " + locationDeparture.ward + ", " + locationDeparture.other
                    tvDialogRuntimeConfirmDestination.text =
                        locationDestination.city + ", " + locationDestination.district +
                                ", " + locationDestination.ward + ", " + locationDestination.other
                    tvDialogRuntimeConfirmTime.text =
                        timeRoute.pickedHour.toString() + ":" + timeRoute.pickedMinute.toString() +
                                " | " + binding.tvHomeSelectDepartureDate.text.toString()
                    countdownTimer = object : CountDownTimer(COUNTDOWN_TIME, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                            val formattedTime = String.format("%02d", seconds)
                            val countdownText =
                                "Vui lòng xác nhận trong khoảng " + ("\nthời gian  $formattedTime").toString()
                            tvDialogRuntimeConfirmText.text = countdownText
                        }

                        override fun onFinish() {
                            onClickConfirm(
                                tvDialogRuntimeTimeCountTicket.text.toString().toInt()
                            )
                            dialog.dismiss()
                        }
                    }
                }
            }
        }

        cancleTime.setOnClickListener {
            dialog.dismiss()
        }

        cancleConfirm.setOnClickListener {
            dialog.dismiss()
        }

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
                ticket = Ticket(
                    phone,
                    locationDeparture,
                    locationDestination,
                    customer.phone.toString(),
                    customer.email.toString(),
                    customer.name.toString(),
                    count,
                    Constants.STATUS_SEARCH_ADMIN,
                    "",
                    timeRoute,
                    date
                )

                db.collection("tickets")
                    .add(ticket)
                    .addOnSuccessListener { documentReference ->
                        ticket.id = documentReference.id
                        db.collection("users").document(phone).collection("tickets")
                            .document(documentReference.id)
                            .set(ticket)
                            .addOnSuccessListener { documentReference ->

                            }
                            .addOnFailureListener { e ->
                            }

                        val bundle = bundleOf(
                            "locationDeparture" to locationDeparture,
                            "locationDestination" to locationDestination,
                            "timeRoute" to timeRoute,
                            "date" to date,
                            "customer" to customer,
                            "ticket" to ticket
                        )
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


//    private fun addListSeacrch(schedule: Schedule) {
//
//    }

    private fun setOpenDialogDate() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = requireActivity().let {
            DatePickerDialog(it, { _, year, month, dayOfMonth ->
                val selectedDateCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }

                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val selectedDate = dateFormat.format(selectedDateCalendar.time)

                binding.tvHomeSelectDepartureDate.text = selectedDate
            }, year, month, day)
        }
//        datePickerDialog.datePicker.minDate = currentDate.timeInMillis
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
