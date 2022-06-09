package com.example.juanjomz.amazonia.data.local.datasource

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.juanjomz.amazonia.data.datasource.PlantImageLocalDataSource
import com.example.juanjomz.amazonia.data.local.LocalStorageService


class PlantImageLocalDataSourceImpl : PlantImageLocalDataSource {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getLocalPlantImage(path: String): List<Bitmap> = LocalStorageService().getLocalImage(path)

    override suspend fun deleteImage(path: String,imagesIndex:List<Int>): Boolean = LocalStorageService().deleteLocalImage(path,imagesIndex)

}