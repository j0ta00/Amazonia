package com.example.juanjomz.amazonia.data.remote

import com.example.juanjomz.amazonia.data.remote.model.PlantDTO
import com.google.gson.JsonElement
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface PlantApiService {
    @POST("all?api-key=2b105W6lnY8BYl7P71d6JoONO")
    suspend fun getPlant(@Body partFile: RequestBody): Response<PlantDTO>

    companion object {
        private const val RETROFIT_PLANTS_API_BASE_URL = "https://my-api.plantnet.org/v2/identify/"

        fun getAPIService(): PlantApiService =
            getRetrofit().create(PlantApiService::class.java)

        private fun getRetrofit(): Retrofit =
            Retrofit.Builder()
                .baseUrl(RETROFIT_PLANTS_API_BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()


    }
}


