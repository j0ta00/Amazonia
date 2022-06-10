package com.example.juanjomz.amazonia.usecases

import com.example.juanjomz.amazonia.data.repository.PlantImageRepository
import com.example.juanjomz.amazonia.domain.PlantBO

class GetSpeciesImagesUseCase (private val plantImageRepository: PlantImageRepository){
    /**
     * Proposito: función invoke que llama al repositorio para conseguir un listado de imagenes remotas
     * @param species : List<PlantBO>
     * @return  List<String>
     * */
    suspend operator fun invoke(species : List<PlantBO>): List<String> = plantImageRepository.getPlantsImages(species)
}