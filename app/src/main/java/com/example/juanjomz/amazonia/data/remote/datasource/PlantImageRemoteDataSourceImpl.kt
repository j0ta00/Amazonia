package com.example.juanjomz.amazonia.data.remote.datasource

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.juanjomz.amazonia.data.datasource.PlantImageRemoteDataSource
import com.example.juanjomz.amazonia.data.datasource.PlantRemoteDataSource
import com.example.juanjomz.amazonia.data.remote.PlantApiService
import com.example.juanjomz.amazonia.data.remote.PlantApiService.Companion.getAPIService
import com.example.juanjomz.amazonia.data.remote.SearchApiService
import com.example.juanjomz.amazonia.domain.PlantBO
import okhttp3.RequestBody
/**
 * Implementación de la intefaz data source encargada de llamar al servicio para obtener las imágenes de las especies
 * @author jjmza
 * */
class PlantImageRemoteDataSourceImpl : PlantImageRemoteDataSource {
    /**
     * Propósito: obtiene las imagenes de una especie llamando al servicio
     * @return List<String> url de las imagenes
     * @param plantName: String nombre de la planta
     * */
    override suspend fun getRemotePlantImage(plantName: String): List<String> = SearchApiService.getImage(plantName)
    @RequiresApi(Build.VERSION_CODES.N)
    /**
     * Propósito: obtiene las imagenes de varias especies llamando al servicio
     * @return List<String> url de las imagenes
     * @param species: List<PlantBO> listado de especies
     * */
    override suspend fun getRemotePlantsImages(species: List<PlantBO>): List<String> = SearchApiService.getImages(species)
}