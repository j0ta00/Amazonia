package com.example.juanjomz.amazonia.data.repository

import com.example.juanjomz.amazonia.data.datasource.PlantImageRemoteDataSource
import com.example.juanjomz.amazonia.data.datasource.PlantRemoteDataSource
import com.example.juanjomz.amazonia.domain.PlantBO
import okhttp3.RequestBody

class PlantImageRepository(private val plantImageRemoteDataSource: PlantImageRemoteDataSource) {
        suspend fun getPlantImage(plantName: String): List<String> = plantImageRemoteDataSource.getRemotePlantImage(plantName)
        suspend fun getPlantsImages(species: List<PlantBO>): List<String> = plantImageRemoteDataSource.getRemotePlantsImages(species)
}