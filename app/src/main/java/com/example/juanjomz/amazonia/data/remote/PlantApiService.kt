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
/**
 * Interfaz encargada de acceder a la api de deteccion de las plantas
 *@author jjmza
 *
 * */
interface PlantApiService {
    /**
     * Propósito: obtiene la planta del de la api
     * @return Response<PlantDTO> listado plantas
     * @param requestbody:RequestBody el body que requiere la api
     * */
    @POST("all?api-key=2b10jyFu75OAAbrp1cjScClP")
    suspend fun getPlant(@Body partFile: RequestBody): Response<PlantDTO>

    companion object {
        private const val RETROFIT_PLANTS_API_BASE_URL = "https://my-api.plantnet.org/v2/identify/"
        /**
         * Propósito: crea y devuelve servicio de las plantas
         * @return PlantApiService
         * */
        fun getAPIService(): PlantApiService =
            getRetrofit().create(PlantApiService::class.java)
        /**
         * Propósito: Construye el servicio de retrofit para hacer la llama a la api
         * @return Retrofit
         * */
        private fun getRetrofit(): Retrofit =
            Retrofit.Builder()
                .baseUrl(RETROFIT_PLANTS_API_BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
    }
}


