package com.example.juanjomz.amazonia.usecases

import com.example.juanjomz.amazonia.data.repository.PlantRepository
import com.example.juanjomz.amazonia.domain.PlantBO
import okhttp3.RequestBody

class GetPlantUseCase(private val plantRepository: PlantRepository) {

 suspend operator fun invoke(requestbody:RequestBody): PlantBO? = plantRepository.getPlant(requestbody)
}