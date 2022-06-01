package com.example.juanjomz.amazonia.data.remote

import android.util.Log
import com.example.juanjomz.amazonia.domain.PlantBO
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class FirestoreService {
    private val db = FirebaseFirestore.getInstance()
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

    suspend fun addSpecie(email:String,plant: PlantBO):Boolean{
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