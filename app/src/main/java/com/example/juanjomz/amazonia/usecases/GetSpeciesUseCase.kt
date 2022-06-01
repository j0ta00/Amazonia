package com.example.juanjomz.amazonia.usecases

import com.example.juanjomz.amazonia.data.repository.PlantRepository
import com.example.juanjomz.amazonia.domain.PlantBO
import okhttp3.RequestBody

class GetSpeciesUseCase(private val plantRepository: PlantRepository) {

    suspend operator fun invoke(email:String): List<PlantBO> = plantRepository.getPlants(email)
}