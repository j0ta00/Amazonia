package com.example.juanjomz.amazonia.domain

import com.google.gson.annotations.SerializedName

data class PlantBO(
    val scientificName: String,
    val commonName: String, val family: String
)