package com.example.ungdungdatxekhach

import com.google.gson.annotations.SerializedName

data class District (
    @SerializedName("Id") val id: String,
    @SerializedName("Name") val name: String,
    @SerializedName("Wards") val wards: List<Ward>,
    @SerializedName("Level") val level: String
)