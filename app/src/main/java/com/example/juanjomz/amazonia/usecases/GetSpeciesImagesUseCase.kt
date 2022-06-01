package com.example.juanjomz.amazonia.usecases

import com.example.juanjomz.amazonia.data.repository.PlantImageRepository
import com.example.juanjomz.amazonia.domain.PlantBO

class GetSpeciesImagesUseCase (private val plantImageRepository: PlantImageRepository){
    suspend operator fun invoke(species : List<PlantBO>): List<String> = plantImageRepository.getPlantsImages(species)
}