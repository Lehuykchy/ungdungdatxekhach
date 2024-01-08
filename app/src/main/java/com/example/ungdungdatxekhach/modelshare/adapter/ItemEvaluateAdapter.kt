package com.example.ungdungdatxekhach.modelshare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.modelshare.Evaluate
import com.example.ungdungdatxekhach.modelshare.Notification
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class ItemEvaluateAdapter : RecyclerView.Adapter<ItemEvaluateAdapter.ItemViewHolder>{
    private lateinit var listItem: ArrayList<Evaluate>
    private lateinit var context: Context
    val db = Firebase.firestore

    interface IClickListener {
        fun onClick(position: Int)
    }

    private lateinit var iClickListener: IClickListener

    constructor(listItem: ArrayList<Evaluate>, context: Context, iClickListener: IClickListener) {
        this.listItem = listItem
        this.context = context
        this.iClickListener = iClickListener
    }

    class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) { // lớp dùng để gán / cập nhật dữ liệu vào các phần tử
        lateinit var tvItemEvaluateNameCustomer: TextView
        lateinit var tvItemEvaluateCommentCustomer: TextView
        lateinit var tvItemEvaluate: TextView
        lateinit var tvItemEvaluateDate: TextView

        init {
            tvItemEvaluateNameCustomer = itemView.findViewById(R.id.tvItemEvaluateNameCustomer)
            tvItemEvaluateCommentCustomer = itemView.findViewById(R.id.tvItemEvaluateCommentCustomer)
            tvItemEvaluate = itemView.findViewById(R.id.tvItemEvaluate)
            tvItemEvaluateDate = itemView.findViewById(R.id.tvItemEvaluateDate)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemEvaluateAdapter.ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_evaluate, parent, false)
        )
    }

    override fun getItemCount(): Int {
        if (listItem != null)
            return listItem.size
        return 0;
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var evaluate = listItem.get(position)
        if (evaluate == null) {
            return
        }
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        holder.tvItemEvaluateNameCustomer.text = evaluate.nameCustomer
        holder.tvItemEvaluateDate.text = dateFormat.format(evaluate.date)
        holder.tvItemEvaluateCommentCustomer.text = evaluate.comment
        holder.tvItemEvaluate.text = evaluate.evaluate.toString()+"/5"
    }
}