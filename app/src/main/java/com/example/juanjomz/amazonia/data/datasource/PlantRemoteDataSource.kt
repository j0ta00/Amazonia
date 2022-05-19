package com.example.juanjomz.amazonia.data.datasource

import com.example.juanjomz.amazonia.domain.PlantBO
import okhttp3.RequestBody

interface PlantRemoteDataSource {

    suspend fun getRemotePlant(requestbody:RequestBody): PlantBO

}