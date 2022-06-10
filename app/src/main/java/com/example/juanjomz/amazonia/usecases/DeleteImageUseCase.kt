package com.example.juanjomz.amazonia.usecases

import android.graphics.Bitmap
import com.example.juanjomz.amazonia.data.repository.LocalImagesRepository

class DeleteImageUseCase(private val localRepository: LocalImagesRepository)  {
    /**
     * Proposito: función invoke que llama al repositorio para borrar una imagen
     * @param path:String,imagesIndex: List<Int>
     * @return Boolean
     * */
    suspend operator fun invoke(path:String,imagesIndex: List<Int>): Boolean = localRepository.deleteImage(path,imagesIndex)
}