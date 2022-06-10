package com.example.juanjomz.amazonia.usecases

import com.example.juanjomz.amazonia.data.repository.PlantRepository
import com.example.juanjomz.amazonia.domain.PlantBO
import okhttp3.RequestBody

class GetSpeciesUseCase(private val plantRepository: PlantRepository) {
    /**
     * Proposito: función invoke que llama al repositorio para conseguir un listado de especies de Firestore
     * @param email:String
     * @return  List<PlantBO>
     * */
    suspend operator fun invoke(email:String): List<PlantBO> = plantRepository.getPlants(email)
}