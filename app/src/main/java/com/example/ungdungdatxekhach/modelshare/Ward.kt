package com.example.ungdungdatxekhach.modelshare

import com.google.gson.annotations.SerializedName

data class Ward (
    @SerializedName("Id") val id: String,
    @SerializedName("Name") val name: String,
    @SerializedName("Level") val level: String
)