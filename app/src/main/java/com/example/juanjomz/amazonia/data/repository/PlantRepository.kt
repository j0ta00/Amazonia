package com.example.juanjomz.amazonia.data.repository

import com.example.juanjomz.amazonia.data.datasource.PlantRemoteDataSource
import com.example.juanjomz.amazonia.domain.PlantBO
import okhttp3.RequestBody

class PlantRepository(private val plantRemoteDataSource: PlantRemoteDataSource){
    suspend fun getPlant(requestbody:RequestBody): List<PlantBO> = plantRemoteDataSource.getRemotePlant(requestbody)
    suspend fun getPlants(email:String): List<PlantBO> = plantRemoteDataSource.getRemotePlants(email)
    suspend fun addPlant(email:String,plant:PlantBO):Boolean  = plantRemoteDataSource.addRemotePlants(email,plant)
}