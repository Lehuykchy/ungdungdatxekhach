package com.example.ungdungdatxekhach.user.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.admin.Constants
import com.example.ungdungdatxekhach.admin.adapter.ItemVehiceManagerAdapter
import com.example.ungdungdatxekhach.admin.model.Vehicle
import com.example.ungdungdatxekhach.modelshare.Schedule
import com.example.ungdungdatxekhach.user.model.Ticket
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class ItemTicketAdapter : RecyclerView.Adapter<ItemTicketAdapter.ItemViewHolder> {
    private lateinit var listItem: ArrayList<Ticket>
    private lateinit var context: Context
    val db = Firebase.firestore

    interface IClickListener {
        fun clickDelete(position: Int)
        fun clickNext(ticket: Ticket, schedule: Schedule)
        fun clickNextOrder(ticket: Ticket)
    }

    private lateinit var iClickListener: IClickListener

    constructor(listItem: ArrayList<Ticket>, context: Context, iClickListener: IClickListener) {
        this.listItem = listItem
        this.context = context
        this.iClickListener = iClickListener
    }

    class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) { // lớp dùng để gán / cập nhật dữ liệu vào các phần tử
        lateinit var tvItemTicketOrderTime: TextView
        lateinit var tvItemTicketOrderStatus: TextView
        lateinit var tvItemTicketOrderDate: TextView
        lateinit var tvItemTicketOrderCode: TextView
        lateinit var tvItemTicketOrderRoute: TextView
        lateinit var tvItemTicketOrderPrice: TextView
        lateinit var lnItemTicketOrder: LinearLayout

        init {
            tvItemTicketOrderTime = itemView.findViewById(R.id.tvItemTicketOrderTime)
            tvItemTicketOrderStatus = itemView.findViewById(R.id.tvItemTicketOrderStatus)
            tvItemTicketOrderDate = itemView.findViewById(R.id.tvItemTicketOrderDate)
            tvItemTicketOrderCode = itemView.findViewById(R.id.tvItemTicketOrderCode)
            tvItemTicketOrderRoute = itemView.findViewById(R.id.tvItemTicketOrderRoute)
            tvItemTicketOrderPrice = itemView.findViewById(R.id.tvItemTicketOrderPrice)
            lnItemTicketOrder = itemView.findViewById(R.id.lnItemTicketOrder)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemTicketAdapter.ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_ticket_order, parent, false)
        )
    }

    override fun getItemCount(): Int {
        if (listItem != null)
            return listItem.size
        return 0;
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var ticket = listItem.get(position)
        if (ticket == null) {
            return
        }
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        if (ticket.status.equals(Constants.STATUS_SEARCH_ADMIN) || ticket.status.equals(Constants.STATUS_WAIT_CUSTOMER)) {
            if (ticket.status.equals(Constants.STATUS_SEARCH_ADMIN)) {
                holder.tvItemTicketOrderStatus.text = "Đang tìm nhà xe"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.tvItemTicketOrderStatus.backgroundTintList =
                        android.content.res.ColorStateList.valueOf(
                            Color.parseColor("#FD8930")
                        )
                }
            }
            if (ticket.status.equals(Constants.STATUS_WAIT_CUSTOMER)) {
                holder.tvItemTicketOrderStatus.text = "Đã tìm thấy nhà xe"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.tvItemTicketOrderStatus.backgroundTintList =
                        android.content.res.ColorStateList.valueOf(
                            Color.parseColor("#FD8930")
                        )
                }
            }
            holder.tvItemTicketOrderRoute.text =
                ticket.departure.other + " - " + ticket.destination.other
            holder.tvItemTicketOrderTime.text =
                ticket.timeRoute.pickedHour.toString() +
                        ":" + ticket.timeRoute.pickedMinute.toString()
            holder.tvItemTicketOrderDate.text = dateFormat.format(ticket.dateDeparture)
            holder.lnItemTicketOrder.setOnClickListener {
                iClickListener.clickNextOrder(ticket)
            }


        } else {
            if (ticket.status.equals(Constants.STATUS_WAIT_PAID)) {
                holder.tvItemTicketOrderStatus.text = "Chờ thanh toán"
            } else if (ticket.status.equals(Constants.STATUS_PAID)) {
                holder.tvItemTicketOrderStatus.text = "Đã thanh toán"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.tvItemTicketOrderStatus.backgroundTintList =
                        android.content.res.ColorStateList.valueOf(
                            Color.parseColor("#4BFA07")
                        )
                }
            } else if (ticket.status.equals(Constants.STATUS_TIMEOUT)) {
                holder.tvItemTicketOrderStatus.text = "Hết Hạn"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.tvItemTicketOrderStatus.backgroundTintList =
                        android.content.res.ColorStateList.valueOf(
                            Color.RED
                        )
                }
            } else if (ticket.status.equals(Constants.STATUS_FINISH)) {

            } else if (ticket.status.equals(Constants.STATUS_SUCCESS)) {
                holder.tvItemTicketOrderStatus.text = "Thành công"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.tvItemTicketOrderStatus.backgroundTintList =
                        android.content.res.ColorStateList.valueOf(
                            Color.BLUE
                        )
                }
            } else if (ticket.status.equals(Constants.STATUS_EVALUATE)) {
                holder.tvItemTicketOrderStatus.text = "Đã đánh giá"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.tvItemTicketOrderStatus.backgroundTintList =
                        android.content.res.ColorStateList.valueOf(
                            Color.BLUE
                        )
                }
            } else if (ticket.status.equals(Constants.STATUS_DESTROY)) {
                holder.tvItemTicketOrderStatus.text = "Đã hủy vé"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.tvItemTicketOrderStatus.backgroundTintList =
                        android.content.res.ColorStateList.valueOf(
                            Color.parseColor("#111111")
                        )
                }
            }
            holder.tvItemTicketOrderPrice.text = Constants.formatCurrency(ticket.totalPrice.toDouble())
            var schedule = Schedule()
            db.collection("routes").document(ticket.routeId).collection("schedules")
                .document(ticket.scheduleId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        schedule = document.toObject<Schedule>()!!
                        if (schedule != null) {
                            holder.tvItemTicketOrderTime.text =
                                schedule.dateRoute.toFormattString()
                            holder.tvItemTicketOrderDate.text = dateFormat.format(schedule.date)
                            holder.tvItemTicketOrderRoute.text =
                                schedule.departureLocation.toString() + " - " + schedule.destinationLocation.toString()

                            holder.lnItemTicketOrder.setOnClickListener {
                                iClickListener.clickNext(ticket, schedule)
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                }
        }
    }
    fun setListData(listData:ArrayList<Ticket>){
        listItem.clear()
        listItem.addAll(listData)
        notifyDataSetChanged()
    }

}