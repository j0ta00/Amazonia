package com.example.juanjomz.amazonia.data.repository

import android.graphics.Bitmap
import com.example.juanjomz.amazonia.data.datasource.PlantImageLocalDataSource
import com.example.juanjomz.amazonia.data.datasource.PlantImageRemoteDataSource

class LocalImagesRepository(private val plantImageLocalDataSource: PlantImageLocalDataSource) {
    /**
     * Propósito: obtiene las imagenes almacenadas en el almacenamiento local llamando a dicho servicio
     * @return List<Bitmap> listado de imagenes
     * @param path: String el path de donde se encuentran dichas imágenes
     * */
    suspend fun getImage(path: String): List<Bitmap> = plantImageLocalDataSource.getLocalPlantImage(path)
    /**
     * Propósito: borra imagenes del almacenamiento local llamando al servicio
     * @return Boolean si la imagen ha sido borrada o no
     * @param path: String,imagesIndex:List<Int> path de donde están las imagenes y el index que ocupa dicha imagen
     * */
    suspend fun deleteImage(path: String,imagesIndex:List<Int>):Boolean = plantImageLocalDataSource.deleteImage(path,imagesIndex)
}