package com.example.juanjomz.amazonia.usecases

import android.graphics.Bitmap
import com.example.juanjomz.amazonia.data.repository.LocalImagesRepository

class GetLocalImagesUseCase(private val localRepository: LocalImagesRepository) {
    suspend operator fun invoke(path: String): List<Bitmap> = localRepository.getImage(path)
}