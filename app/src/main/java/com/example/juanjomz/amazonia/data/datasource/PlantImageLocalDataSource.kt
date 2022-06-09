package com.example.juanjomz.amazonia.data.datasource

import android.graphics.Bitmap

interface PlantImageLocalDataSource {
    suspend fun getLocalPlantImage(path: String): List<Bitmap>
    suspend fun deleteImage(path: String,imagesIndex:List<Int>): Boolean
}