package com.example.juanjomz.amazonia.data.repository

import com.example.juanjomz.amazonia.data.datasource.PlantRemoteDataSource
import com.example.juanjomz.amazonia.domain.PlantBO
import java.io.File

class PlantRepository(private val plantRemoteDataSource: PlantRemoteDataSource){

    suspend fun getPlant(photo: File, organ:String):PlantBO = plantRemoteDataSource.getRemotePlant(photo,organ)
}