package com.example.ungdungdatxekhach.admin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.model.Vehicle
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ItemRouteScheduleAdapter : RecyclerView.Adapter<ItemRouteScheduleAdapter.ItemViewHolder> {
    private lateinit var listItem: ArrayList<Schedule>
    private var vehicleMap = hashMapOf<String, Vehicle>()
    private lateinit var listVehicle: ArrayList<Vehicle>
    private lateinit var context: Context
    private lateinit var route: Route
    val db = Firebase.firestore


    interface IClickListener {
        fun clickDelete(id: Int)
        fun onClick(position : Int)
    }

    private lateinit var iClickListener: IClickListener

    constructor(
        listItem: ArrayList<Schedule>,
        listVehicle: ArrayList<Vehicle>,
        context: Context,
        iClickListener: IClickListener,
        route: Route
    ) {
        this.listItem = listItem
        this.listVehicle = listVehicle
        this.context = context
        this.iClickListener = iClickListener
        this.route = route
    }

    class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) { // lớp dùng để gán / cập nhật dữ liệu vào các phần tử
        lateinit var tvRouteScheduleDepartureTime: TextView
        lateinit var tvRouteScheduleDepartureLocation: TextView
        lateinit var tvRouteScheduleEndLocation: TextView
        lateinit var tvRouteScheduleEndTime: TextView
        lateinit var tvRouteScheduleType: TextView
        lateinit var tvRouteSchedulePrice: TextView
        lateinit var tvRouteScheduleBlank: TextView
        lateinit var itemRouteSchedule: LinearLayout

        init {
            tvRouteScheduleDepartureTime = itemView.findViewById(R.id.tvRouteScheduleDepartureTime)
            tvRouteScheduleDepartureLocation =
                itemView.findViewById(R.id.tvRouteScheduleDepartureLocation)
            tvRouteScheduleEndLocation = itemView.findViewById(R.id.tvRouteScheduleEndLocation)
            tvRouteScheduleEndTime = itemView.findViewById(R.id.tvRouteScheduleEndTime)
            tvRouteScheduleType = itemView.findViewById(R.id.tvRouteScheduleType)
            tvRouteSchedulePrice = itemView.findViewById(R.id.tvRouteSchedulePrice)
            tvRouteScheduleBlank = itemView.findViewById(R.id.tvRouteScheduleBlank)
            itemRouteSchedule = itemView.findViewById(R.id.itemRouteSchedule)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemRouteScheduleAdapter.ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_routes_schedule, parent, false)
        )
    }

    override fun getItemCount(): Int {
        if (listItem != null)
            return listItem.size
        return 0;
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        for (vehicle in listVehicle) {
            vehicleMap[vehicle.id] = vehicle
        }
        var ticket = listItem.get(position)
        holder.tvRouteScheduleDepartureLocation.text = route.departureLocation
        holder.tvRouteScheduleEndLocation.text = route.destination
        holder.tvRouteSchedulePrice.text = route.price
        holder.tvRouteScheduleDepartureTime.text = ticket.dateRoute.pickedHour.toString() +
                ":" + ticket.dateRoute.pickedMinute.toString()
        holder.tvRouteScheduleEndTime.text = ticket.dateRoute.addMinutes(route.totalTime)

        val foundVehicle = vehicleMap[ticket.vehicleId]
        if (foundVehicle != null) {
            holder.tvRouteScheduleType.text =
                foundVehicle.type + " " + foundVehicle.seats.toString() + " chỗ"
            holder.tvRouteScheduleBlank.text = ticket.customerIds.size.toString() +
                    "/" + foundVehicle.seats.toString() + " chỗ trống"
        }
        holder.itemRouteSchedule.setOnClickListener {
            iClickListener.onClick(position)
        }


    }

    fun addTicket(ticket: Schedule) {
        listItem.add(ticket)
        notifyDataSetChanged()
    }
}