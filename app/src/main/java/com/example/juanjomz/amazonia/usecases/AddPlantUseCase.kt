package com.example.juanjomz.amazonia.usecases
import com.example.juanjomz.amazonia.data.repository.PlantRepository
import com.example.juanjomz.amazonia.domain.PlantBO
import okhttp3.RequestBody

class AddPlantUseCase (private val plantRepository: PlantRepository){
    /**
     * Proposito: función invoke que llama al repositorio para añadir una planta
     * @param email:String,plant:PlantBO
     * @return Boolean
     * */
    suspend operator fun invoke(email:String,plant:PlantBO):Boolean = plantRepository.addPlant(email,plant)

}