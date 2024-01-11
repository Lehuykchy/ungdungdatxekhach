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
import com.example.ungdungdatxekhach.modelshare.adapter.Filter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class ItemScheduleAdapter : RecyclerView.Adapter<ItemScheduleAdapter.ItemViewHolder> {
    private lateinit var listItem: ArrayList<Schedule>
    private lateinit var context: Context
    private lateinit var  listFilter: ArrayList<Filter>
    val db = Firebase.firestore


    interface IClickListener {
        fun clickDelete(id: Int)
        fun onClick(schedule: Schedule, route: Route, admin: Admin, vehicle: Vehicle)
    }

    private lateinit var iClickListener: IClickListener

    constructor(
        listFilter: ArrayList<Filter>,
        context: Context,
        iClickListener: IClickListener,
    ) {
        this.listFilter = listFilter
        this.context = context
        this.iClickListener = iClickListener
    }
//    constructor(
//        listItem: ArrayList<Schedule>,
//        context: Context,
//        iClickListener: IClickListener,
//    ) {
//        this.listItem = listItem
//        this.context = context
//        this.iClickListener = iClickListener
//    }

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
        if (listFilter != null)
            return listFilter.size
        return 0;
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var filter = listFilter.get(position)
        if(filter == null){
            return
        }
        var count = 0

        holder.tvScheduleDepartureLocation.text = filter.route.departureLocation
        holder.tvScheduleEndLocation.text = filter.route.destination
        holder.tvSchedulePrice.text = filter.route.price
        holder.tvScheduleEndTime.text = filter.route.totalTime?.let {
            filter.schedule.dateRoute.addMinutes(
                it.toInt()
            )
        }
        holder.tvScheduleDistance.text = filter.route.distance + " Km"
        holder.tvScheduleBusName.text = filter.admin.name
        holder.tvScheduleType.text =
            filter.vehicle.type + " " + filter.vehicle.seats.toString() + " chỗ"
        for(ticket in filter.schedule.customerIds){
            count += ticket.count
        }
        holder.tvScheduleBlank.text = (filter.vehicle.seats - count).toString() +
                "/" + filter.vehicle.seats.toString() + " chỗ trống"

        holder.tvScheduleDepartureTime.text = filter.schedule.dateRoute.pickedHour.toString() +
                ":" + filter.schedule.dateRoute.pickedMinute.toString()
        holder.itemSchedule.setOnClickListener {
            iClickListener.onClick(filter.schedule, filter.route, filter.admin, filter.vehicle)
        }
    }

    fun setData(listItem: ArrayList<Filter>) {
        listFilter.clear()
        listFilter.addAll(listItem)
        notifyDataSetChanged()
    }
    fun getFilter():ArrayList<Filter>{
        return listFilter
    }
}