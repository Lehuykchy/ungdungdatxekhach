package com.example.ungdungdatxekhach.modelshare

import java.io.Serializable

data class ItemCheckBox(var id:String,var name:String, var ischeck: Boolean) : Serializable {
    constructor() : this("","", false)
}