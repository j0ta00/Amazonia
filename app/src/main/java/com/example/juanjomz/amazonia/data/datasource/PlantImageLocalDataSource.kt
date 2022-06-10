package com.example.juanjomz.amazonia.data.datasource

import android.graphics.Bitmap
/**
* Interfaz encargada de las imagenes locales
 * @author jjmz
* */
interface PlantImageLocalDataSource {
    /**
     * Propósito: obtiene las imagenes almacenadas en el almacenamiento local llamando a dicho servicio
     * @return List<Bitmap> listado de imagenes
     * @param path: String el path de donde se encuentran dichas imágenes
     * */
    suspend fun getLocalPlantImage(path: String): List<Bitmap>
    /**
     * Propósito: borra imagenes del almacenamiento local llamando al servicio
     * @return Boolean si la imagen ha sido borrada o no
     * @param path: String,imagesIndex:List<Int> path de donde están las imagenes y el index que ocupa dicha imagen
     * */
    suspend fun deleteImage(path: String,imagesIndex:List<Int>): Boolean
}