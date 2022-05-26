package com.example.juanjomz.amazonia.data.remote.datasource

import com.example.juanjomz.amazonia.data.datasource.PlantImageRemoteDataSource
import com.example.juanjomz.amazonia.data.datasource.PlantRemoteDataSource
import com.example.juanjomz.amazonia.data.remote.PlantApiService
import com.example.juanjomz.amazonia.data.remote.PlantApiService.Companion.getAPIService
import com.example.juanjomz.amazonia.data.remote.SearchApiService
import com.example.juanjomz.amazonia.domain.PlantBO
import okhttp3.RequestBody

class PlantImageRemoteDataSourceImpl : PlantImageRemoteDataSource {
    override suspend fun getRemotePlantImage(plantName: String): List<String> = SearchApiService.getImage(plantName)
}