package com.example.juanjomz.amazonia.data.remote.datasource

import com.example.juanjomz.amazonia.data.datasource.PlantRemoteDataSource
import com.example.juanjomz.amazonia.data.remote.FirestoreService
import com.example.juanjomz.amazonia.data.remote.PlantApiService

import com.example.juanjomz.amazonia.domain.PlantBO


import okhttp3.RequestBody
import okhttp3.Response
import com.example.juanjomz.amazonia.data.remote.model.PlantDTO
import com.google.firebase.firestore.FirebaseFirestore

class PlantRemoteDataSourceImpl : PlantRemoteDataSource {
    private val plantApiService: PlantApiService = PlantApiService.getAPIService()

    override suspend fun getRemotePlant(requestbody: RequestBody): List<PlantBO> {
        val apiResult = plantApiService.getPlant(requestbody)
        val listResult = mutableListOf<PlantBO>()
        if ( apiResult.isSuccessful) {
            val result = (apiResult.body()?.result as? List<Any>)
            for(specie in result!!){
                listResult.add(getResult(specie))
            }
        }
        return listResult
    }

    override suspend fun getRemotePlants(email: String): List<PlantBO> = FirestoreService().getPlants(email)
    override suspend fun addRemotePlants(email:String, plant:PlantBO):Boolean=FirestoreService().addSpecie(email,plant)
    private fun getResult(result: Any): PlantBO {
            val result =
                (result as? Map<Any, Any>)?.get("species") as? Map<Any, Any>
            return PlantBO(
                result?.get("scientificName").toString(),
                (result?.get("commonNames") as? ArrayList<Any>)?.firstOrNull().toString(),
                (result?.get("family") as? ArrayList<Any>)?.firstOrNull().toString()
            ).also {it}
        }


}