package com.example.juanjomz.amazonia.data.network

import com.example.juanjomz.amazonia.data.model.PlantModel
import retrofit2.Response
import retrofit2.http.GET

interface PlantApiClient {
    @GET("/.json")

    suspend fun getPlant(): Response<PlantModel>
}