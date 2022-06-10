package com.example.juanjomz.amazonia.data.repository

import com.example.juanjomz.amazonia.data.datasource.PlantRemoteDataSource
import com.example.juanjomz.amazonia.domain.PlantBO
import okhttp3.RequestBody

class PlantRepository(private val plantRemoteDataSource: PlantRemoteDataSource){
    /**
     * Propósito: obtiene la planta del servicio api de detección
     * @return List<PlantBO> listado plantas
     * @param requestbody:RequestBody el body que requiere la api
     * */
    suspend fun getPlant(requestbody:RequestBody): List<PlantBO> = plantRemoteDataSource.getRemotePlant(requestbody)
    /**
     * Propósito: obtiene las plantas almacenadas en el servicio de firestore
     * @return List<PlantBO> listado plantas que tiene almacenados el usuario
     * @param email: String email del usuario
     * */
    suspend fun getPlants(email:String): List<PlantBO> = plantRemoteDataSource.getRemotePlants(email)
    /**
     * Propósito: añade una nueva especie llamando al servicio de
     * @return List<PlantBO> listado plantas que tiene almacenados el usuario
     * @param email: String email del usuario
     * */
    suspend fun addPlant(email:String,plant:PlantBO):Boolean  = plantRemoteDataSource.addRemotePlants(email,plant)
}