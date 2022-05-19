package com.example.juanjomz.amazonia.data.remote.model

import androidx.lifecycle.GeneratedAdapter
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlantDTO(
    @Json(name="results")
    val result: Any?)