package com.example.ungdungdatxekhach.modelshare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.modelshare.Notification
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class ItemNotificationAdapter : RecyclerView.Adapter<ItemNotificationAdapter.ItemViewHolder>{
    private lateinit var listItem: ArrayList<Notification>
    private lateinit var context: Context
    val db = Firebase.firestore

    interface IClickListener {
        fun onClick(position: Int)
    }

    private lateinit var iClickListener: IClickListener

    constructor(listItem: ArrayList<Notification>, context: Context, iClickListener: IClickListener) {
        this.listItem = listItem
        this.context = context
        this.iClickListener = iClickListener
    }

    class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) { // lớp dùng để gán / cập nhật dữ liệu vào các phần tử
        lateinit var tvItemNotificationTime: TextView
        lateinit var tvItemNotificationInfo: TextView
        lateinit var lnItemNotification: LinearLayout

        init {
            tvItemNotificationInfo = itemView.findViewById(R.id.tvItemNotificationInfo)
            tvItemNotificationTime = itemView.findViewById(R.id.tvItemNotificationTime)
            lnItemNotification = itemView.findViewById(R.id.lnItemNotification)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemNotificationAdapter.ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_notification, parent, false)
        )
    }

    override fun getItemCount(): Int {
        if (listItem != null)
            return listItem.size
        return 0;
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var notification = listItem.get(position)
        if (notification == null) {
            return
        }
        val dateFormat = SimpleDateFormat("HH:mm | dd/MM/yyyy")
        holder.tvItemNotificationInfo.text = notification.infomation
        holder.tvItemNotificationTime.text = dateFormat.format(notification.date)
        holder.lnItemNotification.setOnClickListener {
            iClickListener.onClick(position)
        }
    }

}