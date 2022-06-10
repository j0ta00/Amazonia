package com.example.juanjomz.amazonia.data.remote.model

import androidx.lifecycle.GeneratedAdapter
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
/**
 * Modelo para obtener el resultado de la api, ya que es enorme hay que obtenerlo poco a poco es decir
 * no se puede obtener directamente como un modelo, si no que hay que obtner el Json con any y descomponer el resultado
 * para obtener el modelo que quiero.
 * */
@JsonClass(generateAdapter = true)
data class PlantDTO(
    @Json(name="results")
    val result: Any?)