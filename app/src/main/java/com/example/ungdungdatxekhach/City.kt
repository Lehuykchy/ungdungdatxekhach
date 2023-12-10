package com.example.ungdungdatxekhach

import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("Id") val id: String,
    @SerializedName("Name") val Name: String,
    @SerializedName("Districts") val districts: List<District>
)