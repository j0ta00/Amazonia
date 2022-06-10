package com.example.juanjomz.amazonia.data.repository

import com.example.juanjomz.amazonia.data.datasource.PlantImageRemoteDataSource
import com.example.juanjomz.amazonia.data.datasource.PlantRemoteDataSource
import com.example.juanjomz.amazonia.domain.PlantBO
import okhttp3.RequestBody

class PlantImageRepository(private val plantImageRemoteDataSource: PlantImageRemoteDataSource) {
        /**
         * Propósito: obtiene las imagenes de una especie llamando al servicio
         * @return List<String> url de las imagenes
         * @param plantName: String nombre de la planta
         * */
        suspend fun getPlantImage(plantName: String): List<String> = plantImageRemoteDataSource.getRemotePlantImage(plantName)
        /**
         * Propósito: obtiene las imagenes de varias especies llamando al servicio
         * @return List<String> url de las imagenes
         * @param species: List<PlantBO> listado de especies
         * */
        suspend fun getPlantsImages(species: List<PlantBO>): List<String> = plantImageRemoteDataSource.getRemotePlantsImages(species)
}