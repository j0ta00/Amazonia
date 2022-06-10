package com.example.juanjomz.amazonia.data.datasource

import com.example.juanjomz.amazonia.domain.PlantBO
import okhttp3.RequestBody
/**
* Interfaz encargada de las imágenes remotas
 * @author jjmz
* */
interface PlantImageRemoteDataSource {
    /**
     * Propósito: obtiene las imagenes de una especie llamando al servicio
     * @return List<String> url de las imagenes
     * @param plantName: String nombre de la planta
     * */
    suspend fun getRemotePlantImage(plantName: String): List<String>
    /**
     * Propósito: obtiene las imagenes de varias especies llamando al servicio
     * @return List<String> url de las imagenes
     * @param species: List<PlantBO> listado de especies
     * */
    suspend fun getRemotePlantsImages(species: List<PlantBO>): List<String>

}