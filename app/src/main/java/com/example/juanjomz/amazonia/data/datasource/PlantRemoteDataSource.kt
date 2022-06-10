package com.example.juanjomz.amazonia.data.datasource

import com.example.juanjomz.amazonia.domain.PlantBO
import okhttp3.RequestBody
/**
 * Interfaz encarga de las plantas obtenidas en remoto
 * @author jjmza
 *
 * */
interface PlantRemoteDataSource {
    /**
     * Propósito: obtiene la planta del servicio api de detección
     * @return List<PlantBO> listado plantas
     * @param requestbody:RequestBody el body que requiere la api
     * */
    suspend fun getRemotePlant(requestbody:RequestBody): List<PlantBO>
    /**
     * Propósito: obtiene las plantas almacenadas en el servicio de firestore
     * @return List<PlantBO> listado plantas que tiene almacenados el usuario
     * @param email: String email del usuario
     * */
    suspend fun getRemotePlants(email: String): List<PlantBO>
    /**
     * Propósito: añade una nueva especie llamando al servicio de firestore
     * @return List<PlantBO> listado plantas que tiene almacenados el usuario
     * @param email: String email del usuario
     * */
    suspend fun addRemotePlants(email: String, plant: PlantBO): Boolean
}