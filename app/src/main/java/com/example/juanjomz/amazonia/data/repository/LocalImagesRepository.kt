package com.example.juanjomz.amazonia.data.repository

import android.graphics.Bitmap
import com.example.juanjomz.amazonia.data.datasource.PlantImageLocalDataSource
import com.example.juanjomz.amazonia.data.datasource.PlantImageRemoteDataSource

class LocalImagesRepository(private val plantImageLocalDataSource: PlantImageLocalDataSource) {
    suspend fun getImage(path: String): List<Bitmap> = plantImageLocalDataSource.getLocalPlantImage(path)
    suspend fun deleteImage(path: String,imagesIndex:List<Int>):Boolean = plantImageLocalDataSource.deleteImage(path,imagesIndex)
}