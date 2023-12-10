package com.example.ungdungdatxekhach.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.user.model.Route


class ItemPopularRouteAdapter : RecyclerView.Adapter<ItemPopularRouteAdapter.ItemViewHolder>{


    private lateinit var listItem : ArrayList<Route>
    private lateinit var context : Context

    constructor(listItem : ArrayList<Route>, context : Context) {
        this.listItem = listItem
        this.context = context
    }

    class ItemViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){ // lớp dùng để gán / cập nhật dữ liệu vào các phần tử
        lateinit var tvItemRouteName : TextView
        lateinit var tvItemRouteDistance : TextView
        lateinit var tvItemRouteTime: TextView
        lateinit var tvItemRoutePrice: TextView
        lateinit var lnItem: LinearLayout
        init {
            tvItemRouteName = itemView.findViewById(R.id.tvItemRouteName)
            tvItemRouteDistance = itemView.findViewById(R.id.tvItemRouteDistance)
            tvItemRouteTime = itemView.findViewById(R.id.tvItemRouteTime)
            tvItemRoutePrice = itemView.findViewById(R.id.tvItemRoutePrice)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_popular_route, parent, false))
    }


    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) { //chuyển dữ liệu phần tử vào ViewHolder
        var route = listItem.get(position)
        holder.tvItemRouteName.text = route.name.toString()
        holder.tvItemRouteDistance.text = route.distance.toString()
        holder.tvItemRoutePrice.text = route.price.toString()
        holder.tvItemRouteTime.text = route.time.toString()
    }
}