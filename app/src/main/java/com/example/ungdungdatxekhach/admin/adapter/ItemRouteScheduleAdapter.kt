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
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class ItemRouteScheduleAdapter : RecyclerView.Adapter<ItemRouteScheduleAdapter.ItemViewHolder> {
    private lateinit var listItem: ArrayList<Schedule>
    private lateinit var context: Context
    val db = Firebase.firestore
    private lateinit var route: Route


    interface IClickListener {
        fun clickDelete(id: Int)
        fun onClick(position: Int, route: Route)
    }

    private lateinit var iClickListener: IClickListener

    constructor(
        listItem: ArrayList<Schedule>,
        context: Context,
        iClickListener: IClickListener,
    ) {
        this.listItem = listItem
        this.context = context
        this.iClickListener = iClickListener
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
        var schedule = listItem.get(position)
        route = Route()
        db.collection("routes").document(schedule.routeId)
            .get()
            .addOnSuccessListener { document ->
                route = document.toObject(Route::class.java)!!
                route.id = document.id
                holder.tvRouteScheduleDepartureLocation.text = route?.departureLocation
                holder.tvRouteScheduleEndLocation.text = route?.destination
                holder.tvRouteSchedulePrice.text = route?.price
                holder.tvRouteScheduleEndTime.text = route?.totalTime?.let {
                    schedule.dateRoute.addMinutes(
                        it.toInt()
                    )
                }
                route?.idAdmin?.let {
                    db.collection("admins").document(it).collection("vehicles")
                        .document(schedule.vehicleId)
                        .get()
                        .addOnSuccessListener { document ->
                            val vehicle = document.toObject(Vehicle::class.java)
                            if (vehicle != null) {
                                holder.tvRouteScheduleType.text =
                                    vehicle.type + " " + vehicle.seats.toString() + " chỗ"
                                holder.tvRouteScheduleBlank.text = schedule.customerIds.size.toString() +
                                        "/" + vehicle.seats.toString() + " chỗ trống"
                            }
                            holder.itemRouteSchedule.setOnClickListener {
                                iClickListener.onClick(position, route)
                            }
                        }
                        .addOnFailureListener { exception ->
                        }
                }
            }
            .addOnFailureListener { exception ->
            }

        holder.tvRouteScheduleDepartureTime.text = schedule.dateRoute.pickedHour.toString() +
                ":" + schedule.dateRoute.pickedMinute.toString()

    }

    fun addSchedule(schedule: Schedule) {
        listItem.add(schedule)
        notifyDataSetChanged()
    }
}