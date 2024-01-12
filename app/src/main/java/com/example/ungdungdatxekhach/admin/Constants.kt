package com.example.ungdungdatxekhach.admin

import java.text.DecimalFormat

object Constants {
    const val STATUS_WAIT_PAID = "Chờ thanh toán"//
    const val STATUS_PAID = "Đã thanh toán"//
    const val STATUS_TIMEOUT = "Đã hết hạn"//
    const val STATUS_DESTROY = "Đã hủy"
    const val STATUS_SUCCESS= "Đã đến nơi"//
    const val STATUS_EVALUATE = "Đã đánh giá"//
    const val STATUS_SEARCH_ADMIN = "Đang tìm nhà xe"
    const val STATUS_WAIT_CUSTOMER = "Chờ người dùng xác nhận"
    const val STATUS_FINISH= "Đã kết thúc"//

    const val STATUS_START= "Đã xuất phát"
    const val STATUS_LOADDING_ADMIN= "Chờ người dùng xác nhận đang trên xe"
    const val STATUS_LOADED= "Đã trên xe"
    const val STATUS_NO_START= "Chưa bắt đầu"

    const val URL = "gs://ungdungdatxe-30fd9.appspot.com/images/"

    val DECIMAL_FORMAT = DecimalFormat("#,### đ")

    fun formatCurrency(amount: Double): String {
        return DECIMAL_FORMAT.format(amount)
    }
}