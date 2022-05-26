package com.example.juanjomz.amazonia.usecases

import com.example.juanjomz.amazonia.data.repository.PlantImageRepository
import com.example.juanjomz.amazonia.data.repository.PlantRepository
import com.example.juanjomz.amazonia.domain.PlantBO
import okhttp3.RequestBody

class GetPlantImageUseCase (private val plantImageRepository: PlantImageRepository) {

    suspend operator fun invoke(plantName: String): List<String> = plantImageRepository.getPlantImage(plantName)
}