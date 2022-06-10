package com.example.juanjomz.amazonia.usecases

import com.example.juanjomz.amazonia.data.repository.PlantRepository
import com.example.juanjomz.amazonia.domain.PlantBO
import okhttp3.RequestBody

class GetPlantUseCase(private val plantRepository: PlantRepository) {
 /**
  * Proposito: función invoke que llama al repositorio para conseguir un listado de plantas
  * @param requestbody:RequestBody
  * @return  List<PlantBO>
  * */
 suspend operator fun invoke(requestbody:RequestBody): List<PlantBO> = plantRepository.getPlant(requestbody)
}