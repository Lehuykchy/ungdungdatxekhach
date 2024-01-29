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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ungdungdatxekhach.modelshare.City
import com.example.ungdungdatxekhach.modelshare.Location
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.user.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class ItemLocationRouteAdapter : RecyclerView.Adapter<ItemLocationRouteAdapter.ItemViewHolder> {
    private lateinit var listItem: ArrayList<Location>
    private lateinit var context: Context
    private lateinit var cityList: List<City>

    interface ClickContactListener {
        fun clickLn(id: Int)
    }

    private lateinit var clickContactListener: ClickContactListener

    constructor(
        listItem: ArrayList<Location>,
        context: Context,
        clickContactListener: ClickContactListener
    ) {
        this.listItem = listItem
        this.context = context
        this.clickContactListener = clickContactListener
    }

    class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) { // lớp dùng để gán / cập nhật dữ liệu vào các phần tử
        lateinit var tvItemlocationTime: TextView
        lateinit var tvItemlocationRoute: TextView
        lateinit var lnLocation: RelativeLayout
        lateinit var imgRemove : ImageView

        init {
            tvItemlocationTime = itemView.findViewById(R.id.tvItemlocationTime)
            tvItemlocationRoute = itemView.findViewById(R.id.tvItemLocationRoute)
            lnLocation = itemView.findViewById(R.id.lnItemLocation)
            imgRemove = itemView.findViewById(R.id.imgItemRemoveLocation)


        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var location = listItem.get(position)
        if (location == null) {
            return
        }
        holder.lnLocation.setOnClickListener {
            onClickItem(location)
        }
        if(!location.other.isEmpty()){
            holder.tvItemlocationRoute.text = location.other
        }
        holder.imgRemove.setOnClickListener {
            val dialog : Dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_dialog_confirm);
            dialog.show();

            val ok : Button
            val cancle : Button

            ok = dialog.findViewById(R.id.btnConfirmOk)
            cancle = dialog.findViewById(R.id.btnConfirmCancle)

            ok.setOnClickListener {
                holder.tvItemlocationRoute.text=""
                listItem.removeAt(position)
                notifyDataSetChanged()
                dialog.dismiss()
                clickContactListener.clickLn(position)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemLocationRouteAdapter.ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_location, parent, false)
        )
    }


    override fun getItemCount(): Int {
        if (listItem != null)
            return listItem.size
        return 0;
    }

    fun addLocationRoute(location: Location?) {
        if (location != null) {
            listItem.add(location)
        }
        notifyDataSetChanged()
    }

    private fun onClickItem(location: Location?) {
        val dialog: Dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_location_route);
        dialog.show();

        val ok: Button
        val cancle: Button
        val spinnerDialogCity: Spinner
        val spinnerDialogDistrict: Spinner
        val spinnerDialogWard: Spinner
        val edtDialogOther: EditText

        ok = dialog.findViewById(R.id.btnDialogSave)
        cancle = dialog.findViewById(R.id.btnDialogCancel)
        spinnerDialogCity = dialog.findViewById(R.id.spinnerDialogCity)
        spinnerDialogDistrict = dialog.findViewById(R.id.spinnerDialogDistrict)
        spinnerDialogWard = dialog.findViewById(R.id.spinnerDialogWard)
        edtDialogOther = dialog.findViewById(R.id.edtDialogOther)


        val jsonData = Utils.readJsonFromRawResource(context, R.raw.location)
        cityList = Gson().fromJson(jsonData, object : TypeToken<List<City>>() {}.type)
        val cityNames = cityList.map { it.Name }
        val cityAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, cityNames)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDialogCity.adapter = cityAdapter

        spinnerDialogCity.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    // Lấy danh sách huyện tương ứng với thành phố được chọn và hiển thị chúng
                    val districtNames = cityList[position].districts.map { it.name }
                    val districtAdapter = ArrayAdapter(
                        context,
                        android.R.layout.simple_spinner_item,
                        districtNames
                    )
                    districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerDialogDistrict.adapter = districtAdapter

                    spinnerDialogDistrict.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parentView: AdapterView<*>?,
                                selectedItemView: View?,
                                position1: Int,
                                id: Long
                            ) {
                                // Lấy danh sách xã tương ứng với huyện được chọn và hiển thị chúng
                                val wardNames =
                                    cityList[position].districts[position1].wards.map { it.name }
                                val wardAdapter = ArrayAdapter(
                                    context,
                                    android.R.layout.simple_spinner_item,
                                    wardNames
                                )
                                wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                spinnerDialogWard.adapter = wardAdapter
                            }

                            override fun onNothingSelected(parentView: AdapterView<*>?) {
                                // Không cần xử lý khi không có huyện nào được chọn
                            }
                        }
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {
                    // Không cần xử lý khi không có thành phố nào được chọn
                }
            }



        ok.setOnClickListener {
            if (edtDialogOther.text.isEmpty()) {
                edtDialogOther.error = "Hãy nhập tên"
            } else {
                if (location != null) {
                    location.city = spinnerDialogCity.selectedItem.toString()
                    location.district = spinnerDialogDistrict.selectedItem.toString()
                    location.ward = spinnerDialogWard.selectedItem.toString()
                    location.other = edtDialogOther.text.toString()
                }
                notifyDataSetChanged()
                dialog.dismiss()
            }
        }

        cancle.setOnClickListener {
            dialog.dismiss()
        }

        dialog.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow()?.getAttributes()?.windowAnimations = R.style.DialogAnimation
        dialog.getWindow()?.setGravity(Gravity.BOTTOM);
    }


}