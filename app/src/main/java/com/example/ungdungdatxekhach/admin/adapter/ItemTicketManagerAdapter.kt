package com.example.ungdungdatxekhach.admin.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.user.model.Ticket
import com.example.ungdungdatxekhach.user.model.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Locale

class ItemTicketManagerAdapter : RecyclerView.Adapter<ItemTicketManagerAdapter.ItemViewHolder> {
        private lateinit var listItem : ArrayList<Ticket>
        private lateinit var context: Context
        val db = Firebase.firestore
        private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        interface IClickListener {
            fun onClick(ticket: Ticket)
        }


        private lateinit var iClickListener: IClickListener
        constructor(
            listItem: ArrayList<Ticket>,
            context: Context,
            iClickListener: ItemTicketManagerAdapter.IClickListener,
        ) {
            this.listItem = listItem
            this.context = context
            this.iClickListener = iClickListener
        }

        class ItemViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) { // lớp dùng để gán / cập nhật dữ liệu vào các phần tử
            lateinit var tvItemAdminTicketMount: TextView
            lateinit var tvItemAdminTicketTime: TextView
            lateinit var tvItemAdminTicketName: TextView
            lateinit var tvItemAdminTicketPhone: TextView
            lateinit var imgItemAdminTicket: ImageView
            lateinit var lnItemAdminTicket: RelativeLayout

            init {
                tvItemAdminTicketMount = itemView.findViewById(R.id.tvItemAdminTicketMount)
                tvItemAdminTicketTime = itemView.findViewById(R.id.tvItemAdminTicketTime)
                tvItemAdminTicketName = itemView.findViewById(R.id.tvItemAdminTicketName)
                tvItemAdminTicketPhone = itemView.findViewById(R.id.tvItemAdminTicketPhone)
                imgItemAdminTicket = itemView.findViewById(R.id.imgItemAdminTicket)
                lnItemAdminTicket = itemView.findViewById(R.id.lnItemAdminTicket)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemTicketManagerAdapter.ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_admin_ticket, parent, false)
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
        holder.tvItemAdminTicketName.text = ticket.name
        holder.tvItemAdminTicketPhone.text =ticket.phone
        holder.tvItemAdminTicketMount.text = "x "+ ticket.count.toString()
        holder.tvItemAdminTicketTime.text = timeFormat.format(ticket.createAt)
        holder.lnItemAdminTicket.setOnClickListener {
            iClickListener.onClick(ticket)
        }
        db.collection("users").document(ticket.customerId).get().addOnSuccessListener { document ->
            if (document != null) {
                var user  = document.toObject<User>()!!
                val storagePath = "images/" + user.imageId //
                val storage = FirebaseStorage.getInstance()
                val storageRef = storage.reference.child(storagePath)
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    Glide.with(context)
                        .load(downloadUrl)
                        .into(holder.imgItemAdminTicket)
                }.addOnFailureListener { exception ->
                    Log.e("Firebase Storage", "Error getting download URL: ${exception.message}")
                }
            }
        }.addOnFailureListener { exception ->
        }
    }
}