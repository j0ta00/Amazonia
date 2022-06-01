package com.example.juanjomz.amazonia.usecases
import com.example.juanjomz.amazonia.data.repository.PlantRepository
import com.example.juanjomz.amazonia.domain.PlantBO
import okhttp3.RequestBody

class AddPlantUseCase (private val plantRepository: PlantRepository){
    suspend operator fun invoke(email:String,plant:PlantBO):Boolean = plantRepository.addPlant(email,plant)

}