package com.example.juanjomz.amazonia.data.remote

import android.util.Log
import com.example.juanjomz.amazonia.domain.PlantBO
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception
/**
 * Servicio de firestore, encargado del almacenamiento online
 *@author jjmza
 *
 * */
class FirestoreService {
    private val db = FirebaseFirestore.getInstance()
    /**
     * Propósito: obtiene la planta de firestore
     * @return List<PlantBO> listado plantas
     * @param requestbody:RequestBody el body que requiere la api
     * */
         suspend fun getPlants(email: String): List<PlantBO> {
        val plantList = mutableListOf<PlantBO>()
        email.let { db.collection("users").document(it).collection("species").get().addOnSuccessListener { species ->
            for(plant in species.documents ){
                if(!plantList.contains(PlantBO(plant.get("ScientificName") as String,plant.get("CommonName") as String,plant.get("Family") as String))) {
                    plantList.add(PlantBO(plant.get("ScientificName") as String,
                        plant.get("CommonName") as String,
                        plant.get("Family") as String))
                }
            }
        }.await()}
        return plantList
    }
    /**
     * Propósito: añade una nueva especie a firebase
     * @return List<PlantBO> listado plantas que tiene almacenados el usuario
     * @param email: String email del usuario
     * */
    suspend fun addSpecie(email:String,plant: PlantBO):Boolean{ //no recibe un dto ya que firestore no acepta json si no que se tiene que insertar tal y como se hace en este método por lo que hacer un mapper para convertir una clase BO a una DTO idéntica no le veía sentido ni era necesario, ocurre lo mismo en el método de arriba
        var result=true
      try{ db.collection("users").document(email).collection("species").document().set(
    hashMapOf(
        "CommonName" to plant.commonName,
        "ScientificName" to plant.scientificName,
        "Family" to plant.family
    )).await()
      }catch(e: Exception){
          result=false
      }
        return result
    }
}