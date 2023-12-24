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
        if (ticket.statusPay) {
            holder.tvItemTicketOrderStatus.text = "Đã thanh toán"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.tvItemTicketOrderStatus.backgroundTintList= android.content.res.ColorStateList.valueOf(
                    Color.parseColor("#4BFA07"))
            }
        }else{
            holder.tvItemTicketOrderStatus.text = "Chờ thanh toán"
        }
        holder.tvItemTicketOrderPrice.text = ticket.totalPrice + " đ"


        var schedule = Schedule()
        db.collection("routes").document(ticket.routeId).collection("schedules").document(ticket.scheduleId)
            .get()
            .addOnSuccessListener { document ->
                if(document != null){
                    schedule = document.toObject<Schedule>()!!
                    if (schedule != null) {
                        holder.tvItemTicketOrderTime.text = schedule.dateRoute.pickedHour.toString() +
                                ":" +schedule.dateRoute.pickedMinute.toString()
                        holder.tvItemTicketOrderDate.text = dateFormat.format(schedule.date)
                        holder.tvItemTicketOrderRoute.text = schedule.departureLocation.toString() + " - " + schedule.destinationLocation.toString()

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