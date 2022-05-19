package com.example.juanjomz.amazonia.data.remote.datasource

import com.example.juanjomz.amazonia.data.datasource.PlantRemoteDataSource
import com.example.juanjomz.amazonia.data.remote.PlantApiService

import com.example.juanjomz.amazonia.domain.PlantBO


import okhttp3.RequestBody

class PlantRemoteDataSourceImpl : PlantRemoteDataSource {
    private val plantApiService : PlantApiService = PlantApiService.getAPIService()
    override suspend fun getRemotePlant(requestbody:RequestBody): PlantBO {
        val result=plantApiService.getPlant(requestbody)
        var plantBO = PlantBO("","","")
        if(result.isSuccessful) {
            val result = (((result.body()?.result as? List<Any>)?.firstOrNull()  as? Map<Any,Any>)?.get("species") as? Map<Any,Any>)
            plantBO = PlantBO(result?.get("scientificName").toString(),(result?.get("commonNames") as? ArrayList<Any>)?.firstOrNull().toString(),(result?.get("family") as? ArrayList<Any>)?.firstOrNull().toString())
        }
        return plantBO
    }
}