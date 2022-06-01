package com.example.juanjomz.amazonia.data.datasource

import com.example.juanjomz.amazonia.domain.PlantBO
import okhttp3.RequestBody

interface PlantImageRemoteDataSource {

    suspend fun getRemotePlantImage(plantName: String): List<String>
    suspend fun getRemotePlantsImages(species: List<PlantBO>): List<String>

}