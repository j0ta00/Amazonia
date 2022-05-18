package com.example.juanjomz.amazonia.data.remote.model

import com.google.gson.annotations.SerializedName

data class PlantDTO(
    @SerializedName("scientificName")
    val scientificName: String?,
    @SerializedName("commonName")
    val commonName: String?,
    @SerializedName("family") val family: String?)