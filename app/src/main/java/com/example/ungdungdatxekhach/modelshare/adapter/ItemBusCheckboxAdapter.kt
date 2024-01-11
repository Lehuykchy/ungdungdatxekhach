package com.example.ungdungdatxekhach.modelshare.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.modelshare.Evaluate
import com.example.ungdungdatxekhach.modelshare.ItemCheckBox
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class ItemBusCheckboxAdapter : RecyclerView.Adapter<ItemBusCheckboxAdapter.ItemViewHolder>{
    private lateinit var listItem: ArrayList<ItemCheckBox>
    private lateinit var context: Context

    interface IClickListener {
        fun onClick(position: Int, ischecked: Boolean)
    }
    private lateinit var iClickListener: ItemBusCheckboxAdapter.IClickListener



    constructor(listItem: ArrayList<ItemCheckBox>, context: Context, iClickListener: ItemBusCheckboxAdapter.IClickListener) {
        this.listItem = listItem
        this.context = context
        this.iClickListener = iClickListener
    }

    class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) { // lớp dùng để gán / cập nhật dữ liệu vào các phần tử
        lateinit var cbItemBusCheckBox: CheckBox
        lateinit var tvItemBusCheckBoxName: TextView
        lateinit var rltItemBusCheckBox: RelativeLayout

        init {
            cbItemBusCheckBox = itemView.findViewById(R.id.cbItemBusCheckBox)
            tvItemBusCheckBoxName = itemView.findViewById(R.id.tvItemBusCheckBoxName)
            rltItemBusCheckBox = itemView.findViewById(R.id.rltItemBusCheckBox)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemBusCheckboxAdapter.ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_bus_checkbox, parent, false)
        )
    }

    override fun getItemCount(): Int {
        if (listItem != null)
            return listItem.size
        return 0;
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var itemCheckBox = listItem.get(position)
        if (itemCheckBox == null) {
            return
        }
        holder.tvItemBusCheckBoxName.text = itemCheckBox.name
        holder.cbItemBusCheckBox.isChecked = itemCheckBox.ischeck
        holder.rltItemBusCheckBox.setOnClickListener {
            if(holder.cbItemBusCheckBox.isChecked){
                holder.cbItemBusCheckBox.isChecked=false
                iClickListener.onClick(position, false)
            }else{
                holder.cbItemBusCheckBox.isChecked = true
                iClickListener.onClick(position, true)
            }
        }
    }
    fun getlistData() : ArrayList<ItemCheckBox>{
        return listItem
    }
}