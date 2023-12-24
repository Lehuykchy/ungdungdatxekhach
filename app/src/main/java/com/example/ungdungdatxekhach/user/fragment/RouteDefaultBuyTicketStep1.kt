package com.example.ungdungdatxekhach.user.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.ungdungdatxekhach.modelshare.Location
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.databinding.FragmentRouteDefaultBuyticketStep1Binding
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.example.ungdungdatxekhach.user.model.Ticket
import java.util.Date


class RouteDefaultBuyTicketStep1 : Fragment() {
    private lateinit var binding: FragmentRouteDefaultBuyticketStep1Binding
    private lateinit var route: Route
    private lateinit var schedule: Schedule
    private lateinit var departure: String
    private lateinit var destination: String
    private lateinit var phone: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRouteDefaultBuyticketStep1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val receivedBundle = arguments
        if (receivedBundle != null && receivedBundle.containsKey("route") && receivedBundle.containsKey(
                "schedule"
            )
        ) {
            route = receivedBundle.getSerializable("route") as Route
            schedule = receivedBundle.getSerializable("schedule") as Schedule
            phone = receivedBundle.getSerializable("phone") as String
        }
        departure = route.location.get(0).other.toString()
        destination = route.location.get(route.location.size - 1).other.toString()

        setClickAddTicket()
        setspinnerTvBuyTicketStep1Departure()
        setspinnerTvBuyTicketStep1Destination()
        binding.btnBuyTicketStep1Confirm.setOnClickListener {
            if (ischeck()) {
                val ticket = Ticket(
                    phone,
                    schedule.id,
                    route.id,
                    binding.edtBuyTicketStep1Departure.text.toString(),
                    binding.edtBuyTicketStep1Destination.text.toString(),
                    binding.tvBuyTicketStep1CountTicket.text.toString().toInt(),
                    Date(),
                    (binding.tvBuyTicketStep1CountTicket.text.toString().toInt() * route.price.toString().toInt()).toString(),
                )
                val bundle = bundleOf("route" to route, "schedule" to schedule, "ticket" to ticket)
                val navController = activity?.findNavController(R.id.framelayout)
                navController?.navigate(
                    R.id.action_routeDefaultBuyTicketStep1_to_routeDefaultBuyTicketStep2,
                    bundle
                )
            }
        }
        binding.imgBuyTicketStep1BackUser.setOnClickListener {
            val navController = activity?.findNavController(R.id.framelayout)
            navController?.popBackStack()
        }
        binding.lnBuyTicketStep1Schedule.setOnClickListener {
            onClickSchedule()
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

        val locationAdapter = LocationAdapter(requireActivity(), route.location)
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

    private fun ischeck(): Boolean {
        if (binding.tvBuyTicketStep1CountTicket.text.toString() == "0") {
            Toast.makeText(requireActivity(), "Chọn số vé", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.edtBuyTicketStep1Departure.text.toString().isEmpty()) {
            binding.edtBuyTicketStep1Departure.error = "Nhập điểm đi"
            return false
        }
        if (binding.edtBuyTicketStep1Destination.text.toString().isEmpty()) {
            binding.edtBuyTicketStep1Destination.error = "Nhập điểm đến"
            return false
        }
        return true
    }

    private fun setClickAddTicket() {
        binding.imgBuyTicketStep1MinusTicket.setOnClickListener {
            if (binding.tvBuyTicketStep1CountTicket.text.toString().toInt() > 0) {
                val i: Int = binding.tvBuyTicketStep1CountTicket.text.toString().toInt() - 1
                binding.tvBuyTicketStep1CountTicket.setText(i.toString())
                binding.tvBuyTicketStep1CountSeat.setText(i.toString())
                binding.tvBuyTicketStep1TotalMoney.setText((i * route.price.toInt()).toString() + " đ")
            }
        }

        binding.imgBuyTicketStep1AddTicket.setOnClickListener {
            val i: Int = binding.tvBuyTicketStep1CountTicket.text.toString().toInt() + 1
            binding.tvBuyTicketStep1CountTicket.setText(i.toString())
            binding.tvBuyTicketStep1CountSeat.setText(i.toString()+ " vé")
            binding.tvBuyTicketStep1TotalMoney.setText((i * route.price.toInt()).toString() + " đ")
        }
    }

    private fun setspinnerTvBuyTicketStep1Destination() {
        val options = arrayOf("Tại bến", "Tại nhà")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTvBuyTicketStep1Destination.adapter = adapter
        binding.spinnerTvBuyTicketStep1Destination.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedOption = options[position]
                    when (selectedOption) {
                        "Tại bến" -> {
                            binding.edtBuyTicketStep1Destination.setText(destination)

                        }

                        "Tại nhà" -> {
                            binding.edtBuyTicketStep1Destination.setText("")
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Không có lựa chọn nào được chọn
                }
            }
    }

    private fun setspinnerTvBuyTicketStep1Departure() {
        val options = arrayOf("Tại bến", "Dọc đường")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTvBuyTicketStep1Departure.adapter = adapter
        binding.spinnerTvBuyTicketStep1Departure.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedOption = options[position]
                    when (selectedOption) {
                        "Tại bến" -> {
                            binding.edtBuyTicketStep1Departure.setText(departure)
                        }

                        "Dọc đường" -> {
                            binding.edtBuyTicketStep1Departure.setText("")
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Không có lựa chọn nào được chọn
                }
            }
    }

    class LocationAdapter(context: Context, private val locations: List<Location>) :
        ArrayAdapter<Location>(context, android.R.layout.simple_list_item_1, locations) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            val location = getItem(position)

            val locationName = location?.other
            val textView = view.findViewById<TextView>(android.R.id.text1)
            textView.text = locationName

            return view
        }
    }

}