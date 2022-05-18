package com.example.juanjomz.amazonia.data.datasource

import com.example.juanjomz.amazonia.domain.PlantBO
import java.io.File

interface PlantRemoteDataSource {

    suspend fun getRemotePlant(photo: File, organ:String):PlantBO

}