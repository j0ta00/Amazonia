package com.example.juanjomz.amazonia.usecases

import com.example.juanjomz.amazonia.data.repository.PlantRepository
import com.example.juanjomz.amazonia.domain.PlantBO
import java.io.File

class GetPlantUseCase(private val plantRepository: PlantRepository) {

 suspend operator fun invoke(photo:File,organ:String):PlantBO = plantRepository.getPlant(photo,organ)
}