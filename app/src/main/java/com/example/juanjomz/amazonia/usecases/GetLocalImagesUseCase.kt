package com.example.juanjomz.amazonia.usecases

import android.graphics.Bitmap
import com.example.juanjomz.amazonia.data.repository.LocalImagesRepository

class GetLocalImagesUseCase(private val localRepository: LocalImagesRepository) {
    /**
     * Proposito: función invoke que llama al repositorio para obtener las imágenes
     * @param path: String
     * @return List<Bitmap>
     * */
    suspend operator fun invoke(path: String): List<Bitmap> = localRepository.getImage(path)
}