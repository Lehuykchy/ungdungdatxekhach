package com.example.ungdungdatxekhach.admin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.Constants
import com.example.ungdungdatxekhach.modelshare.Route


class ItemPopularRouteAdminAdapter :
    RecyclerView.Adapter<ItemPopularRouteAdminAdapter.ItemViewHolder> {
    private lateinit var listItem: ArrayList<Route>
    private lateinit var context: Context

    interface OnClickListener{
        fun onCLick(postion : Int)
    }
    private lateinit var onClickListener: OnClickListener

    constructor(listItem: ArrayList<Route>, context: Context, onClickListener: OnClickListener) {
        this.listItem = listItem
        this.context = context
        this.onClickListener = onClickListener
    }

    class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) { // lớp dùng để gán / cập nhật dữ liệu vào các phần tử
        lateinit var tvItemRouteName: TextView
        lateinit var tvItemRouteDistance: TextView
        lateinit var tvItemRouteTime: TextView
        lateinit var tvItemRoutePrice: TextView
        lateinit var lnItem: RelativeLayout

        init {
            tvItemRouteName = itemView.findViewById(R.id.tvItemRouteName)
            tvItemRouteDistance = itemView.findViewById(R.id.tvItemRouteDistance)
            tvItemRouteTime = itemView.findViewById(R.id.tvItemRouteTime)
            tvItemRoutePrice = itemView.findViewById(R.id.tvItemRoutePrice)
            lnItem = itemView.findViewById(R.id.lnItemRoute)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_popular_route, parent, false)
        )
    }


    override fun getItemCount(): Int {
        if (listItem != null)
            return listItem.size
        return 0;
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int
    ) { //chuyển dữ liệu phần tử vào ViewHolder
        var route = listItem.get(position)
        val minute = route.distance.toInt()
        val (hour, minuteRemain) = changeTime(minute)
        holder.tvItemRouteName.text =
            route.departureLocation.toString() + " - " + route.destination.toString()
        holder.tvItemRouteDistance.text = route.distance.toString() + "km"
        holder.tvItemRoutePrice.text = Constants.formatCurrency(route.price.toString().toDouble())
        holder.tvItemRouteTime.text = hour.toString() + " giờ " + minuteRemain + " phút"
        holder.lnItem.setOnClickListener {
            onClickListener.onCLick(position)
        }
    }

    fun changeTime(
        minute: Int
    ): Pair<Int, Int> {
        val hour = minute / 60
        val minuteRemain = minute % 60
        return Pair(hour, minuteRemain)
    }
}