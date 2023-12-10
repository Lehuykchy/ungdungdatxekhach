package com.example.ungdungdatxekhach.admin.adapter

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.Time

class ItemTimeAdapter : RecyclerView.Adapter<ItemTimeAdapter.ItemViewHolder>{
    private lateinit var listItem: ArrayList<Time>
    private lateinit var context: Context
    interface IClickListener{
        fun clickDelete(id : Int)
    }

    private lateinit var iClickListener: IClickListener

    constructor(listItem: ArrayList<Time>, context: Context, iClickListener: IClickListener) {
        this.listItem = listItem
        this.context = context
        this.iClickListener = iClickListener
    }
    class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) { // lớp dùng để gán / cập nhật dữ liệu vào các phần tử
        lateinit var tvItemTime: TextView
        lateinit var imgItemTimeDelte: ImageView

        init {
            tvItemTime = itemView.findViewById(R.id.tvItemTime)
            imgItemTimeDelte = itemView.findViewById(R.id.imgItemTimeDelete)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemTimeAdapter.ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_time, parent, false)
        )
    }

    override fun getItemCount(): Int {
        if (listItem != null)
            return listItem.size
        return 0;
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var time = listItem.get(position)
        if(time==null){
            return
        }

        val pickedHour = time.pickedHour
        val pickedMinute = time.pickedMinute
        holder.tvItemTime.text = pickedHour.toString() + ":" +pickedMinute.toString()

        holder.imgItemTimeDelte.setOnClickListener {
            val dialog : Dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_dialog_confirm);
            dialog.show();

            val ok : Button
            val cancle : Button

            ok = dialog.findViewById(R.id.btnConfirmOk)
            cancle = dialog.findViewById(R.id.btnConfirmCancle)

            ok.setOnClickListener {
                listItem.remove(time)
                notifyDataSetChanged()
                dialog.dismiss()
            }

            cancle.setOnClickListener {
                dialog.dismiss()
            }

            dialog.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow()?.getAttributes()?.windowAnimations = R.style.DialogAnimation
            dialog.getWindow()?.setGravity(Gravity.BOTTOM);
        }
    }

    fun addTime(time: Time?) {
        if (time != null) {
            listItem.add(time)
        }
        notifyDataSetChanged()
    }
}