package com.example.juanjomz.amazonia.data.repository

import com.example.juanjomz.amazonia.data.datasource.PlantRemoteDataSource
import com.example.juanjomz.amazonia.domain.PlantBO
import okhttp3.RequestBody

class PlantRepository(private val plantRemoteDataSource: PlantRemoteDataSource){

    suspend fun getPlant(requestbody:RequestBody): PlantBO? = plantRemoteDataSource.getRemotePlant(requestbody)
}