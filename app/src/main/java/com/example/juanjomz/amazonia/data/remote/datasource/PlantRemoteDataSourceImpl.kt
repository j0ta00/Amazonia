package com.example.juanjomz.amazonia.data.remote.datasource

import com.example.juanjomz.amazonia.data.datasource.PlantRemoteDataSource
import com.example.juanjomz.amazonia.domain.PlantBO
import java.io.File

class PlantRemoteDataSourceImpl : PlantRemoteDataSource {

    suspend override fun getRemotePlant(photo: File, organ: String): PlantBO =
        ApiService.getPlant(photo,organ).toBO()

}