package com.example.ungdungdatxekhach.admin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.model.Vehicle


class ItemVehiceManagerAdapter : RecyclerView.Adapter<ItemVehiceManagerAdapter.ItemViewHolder>{
    private lateinit var listItem: ArrayList<Vehicle>
    private lateinit var context: Context
    interface IClickListener{
        fun clickDelete(id : Int)
        fun clickNext(id: Int)
    }

    private lateinit var iClickListener: IClickListener

    constructor(listItem: ArrayList<Vehicle>, context: Context, iClickListener: IClickListener) {
        this.listItem = listItem
        this.context = context
        this.iClickListener = iClickListener
    }
    class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) { // lớp dùng để gán / cập nhật dữ liệu vào các phần tử
        lateinit var tvItemVehiceLicensePlate: TextView
        lateinit var tvItemVehiceType: TextView
        lateinit var tvItemVehiceSeat: TextView

        init {
            tvItemVehiceLicensePlate =  itemView.findViewById(R.id.tvItemVehiceLicensePlate)
            tvItemVehiceType = itemView.findViewById(R.id.tvItemVehiceType)
            tvItemVehiceSeat = itemView.findViewById(R.id.tvItemVehiceSeat)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemVehiceManagerAdapter.ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_vehice, parent, false)
        )
    }

    override fun getItemCount(): Int {
        if (listItem != null)
            return listItem.size
        return 0;
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var vehice = listItem.get(position)
        if(vehice == null){
            return
        }
        holder.tvItemVehiceLicensePlate.text = vehice.licensePlate
        holder.tvItemVehiceType.text = vehice.type
        holder.tvItemVehiceSeat.text = vehice.seats.toString()
    }

    fun addVehice(vehicle : Vehicle){
        listItem.add(vehicle)
        notifyDataSetChanged()
    }
}