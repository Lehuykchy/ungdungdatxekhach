package com.example.ungdungdatxekhach.admin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.model.Admin
import com.example.ungdungdatxekhach.admin.model.Vehicle
import com.example.ungdungdatxekhach.modelshare.Route
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class ItemScheduleAdapter : RecyclerView.Adapter<ItemScheduleAdapter.ItemViewHolder> {
    private lateinit var listItem: ArrayList<Schedule>
    private lateinit var context: Context
    val db = Firebase.firestore


    interface IClickListener {
        fun clickDelete(id: Int)
        fun onClick(position: Int, route: Route, admin: Admin)
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
        lateinit var tvScheduleDepartureTime: TextView
        lateinit var tvScheduleDepartureLocation: TextView
        lateinit var tvScheduleEndLocation: TextView
        lateinit var tvScheduleEndTime: TextView
        lateinit var tvScheduleType: TextView
        lateinit var tvSchedulePrice: TextView
        lateinit var tvScheduleBlank: TextView
        lateinit var tvScheduleEvaluate: TextView
        lateinit var tvScheduleDistance: TextView
        lateinit var tvScheduleBusName: TextView
        lateinit var itemSchedule: LinearLayout

        init {
            tvScheduleDepartureTime = itemView.findViewById(R.id.tvScheduleDepartureTime)
            tvScheduleDepartureLocation =
                itemView.findViewById(R.id.tvScheduleDepartureLocation)
            tvScheduleEndLocation = itemView.findViewById(R.id.tvScheduleEndLocation)
            tvScheduleEndTime = itemView.findViewById(R.id.tvScheduleEndTime)
            tvScheduleType = itemView.findViewById(R.id.tvScheduleType)
            tvSchedulePrice = itemView.findViewById(R.id.tvSchedulePrice)
            tvScheduleBlank = itemView.findViewById(R.id.tvScheduleBlank)
            tvScheduleEvaluate = itemView.findViewById(R.id.tvScheduleEvaluate)
            tvScheduleDistance = itemView.findViewById(R.id.tvScheduleDistance)
            itemSchedule = itemView.findViewById(R.id.itemSchedule)
            tvScheduleBusName = itemView.findViewById(R.id.tvScheduleBusName)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_schedule, parent, false)
        )
    }

    override fun getItemCount(): Int {
        if (listItem != null)
            return listItem.size
        return 0;
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var schedule = listItem.get(position)

        db.collection("routes").document(schedule.routeId)
            .get()
            .addOnSuccessListener { document ->
                var admin = Admin()
                var route = document.toObject(Route::class.java)!!
                route.id = document.id
                holder.tvScheduleDepartureLocation.text = route?.departureLocation
                holder.tvScheduleEndLocation.text = route?.destination
                holder.tvSchedulePrice.text = route?.price
                holder.tvScheduleEndTime.text = route?.totalTime?.let {
                    schedule.dateRoute.addMinutes(
                        it.toInt()
                    )
                }
                holder.tvScheduleDistance.text = route.distance + " Km"

                route?.idAdmin?.let {
                    db.collection("admins").document(it)
                        .get()
                        .addOnSuccessListener { document ->
                            admin = document.toObject<Admin>()!!
                            holder.tvScheduleBusName.text = admin?.name
                        }
                        .addOnFailureListener { exception ->
                        }

                    db.collection("admins").document(it).collection("vehicles")
                        .document(schedule.vehicleId)
                        .get()
                        .addOnSuccessListener { document ->
                            val vehicle = document.toObject(Vehicle::class.java)
                            if (vehicle != null) {
                                holder.tvScheduleType.text =
                                    vehicle.type + " " + vehicle.seats.toString() + " chỗ"
                                holder.tvScheduleBlank.text = schedule.customerIds.size.toString() +
                                        "/" + vehicle.seats.toString() + " chỗ trống"
                            }
                            holder.itemSchedule.setOnClickListener {
                                iClickListener.onClick(position, route, admin)
                            }
                        }
                        .addOnFailureListener { exception ->
                        }

                }
            }
            .addOnFailureListener { exception ->
            }

        holder.tvScheduleDepartureTime.text = schedule.dateRoute.pickedHour.toString() +
                ":" + schedule.dateRoute.pickedMinute.toString()

    }

    fun addSchedule(schedule: Schedule) {
        listItem.add(schedule)
        notifyDataSetChanged()
    }
}