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
import com.example.ungdungdatxekhach.user.model.Ticket
import java.text.SimpleDateFormat

class ItemTicketOrderAdapter : RecyclerView.Adapter<ItemTicketOrderAdapter.ItemViewHolder> {
    private lateinit var listItem : ArrayList<Ticket>
    private lateinit var context: Context
    interface IClickListener {
        fun onClick(ticket: Ticket)
    }

    private lateinit var iClickListener: IClickListener
    constructor(
        listItem: ArrayList<Ticket>,
        context: Context,
        iClickListener: ItemTicketOrderAdapter.IClickListener,
    ) {
        this.listItem = listItem
        this.context = context
        this.iClickListener = iClickListener
    }

    class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) { // lớp dùng để gán / cập nhật dữ liệu vào các phần tử
        lateinit var tvItemAdminTicketOrderTime: TextView
        lateinit var tvItemAdminTicketOrderCountTicket: TextView
        lateinit var tvItemAdminTicketOrderDate: TextView
        lateinit var tvItemAdminTicketOrderName: TextView
        lateinit var tvItemAdminTicketOrderDeparture: TextView
        lateinit var tvItemAdminTicketOrderDestination: TextView
        lateinit var lnItemAdminTicketOrder: LinearLayout

        init {
            tvItemAdminTicketOrderTime = itemView.findViewById(R.id.tvItemAdminTicketOrderTime)
            tvItemAdminTicketOrderCountTicket = itemView.findViewById(R.id.tvItemAdminTicketOrderCountTicket)
            tvItemAdminTicketOrderDate = itemView.findViewById(R.id.tvItemAdminTicketOrderDate)
            tvItemAdminTicketOrderName = itemView.findViewById(R.id.tvItemAdminTicketOrderName)
            tvItemAdminTicketOrderDeparture = itemView.findViewById(R.id.tvItemAdminTicketOrderDeparture)
            tvItemAdminTicketOrderDestination = itemView.findViewById(R.id.tvItemAdminTicketOrderDestination)
            lnItemAdminTicketOrder = itemView.findViewById(R.id.lnItemAdminTicketOrder)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemTicketOrderAdapter.ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_admin_ticket_order, parent, false)
        )
    }

    override fun getItemCount(): Int {
        if (listItem != null)
            return listItem.size
        return 0;
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var ticket = listItem.get(position)
        if(ticket==null){
            return
        }
        val formatDate = SimpleDateFormat("dd/MM/yyyy")
        holder.tvItemAdminTicketOrderTime.text= ticket.timeRoute.toFormattString()
        holder.tvItemAdminTicketOrderCountTicket.text = "x "+ ticket.count.toString() +" vé"
        holder.tvItemAdminTicketOrderDate.text = formatDate.format(ticket.dateDeparture)
        holder.tvItemAdminTicketOrderName.text = ticket.name
        holder.tvItemAdminTicketOrderDeparture.text = ticket.departure.city + "/" + ticket.departure.district +
                "/" + ticket.departure.ward + "/" +ticket.departure.other
        holder.tvItemAdminTicketOrderDestination.text = ticket.destination.city + "/" + ticket.destination.district +
                "/" + ticket.destination.ward + "/" +ticket.destination.other
        holder.lnItemAdminTicketOrder.setOnClickListener {
            iClickListener.onClick(ticket)
        }
    }
}