package com.example.juanjomz.amazonia.data.local.datasource

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.juanjomz.amazonia.data.datasource.PlantImageLocalDataSource
import com.example.juanjomz.amazonia.data.local.LocalStorageService
/**
 * Implementación de la interfaz data source de imágenes locales
 * @author jjmza
 *
 * */

class PlantImageLocalDataSourceImpl : PlantImageLocalDataSource {

    @RequiresApi(Build.VERSION_CODES.O)
    /**
     * Propósito: borra imagenes del almacenamiento local llamando al servicio
     * @return Boolean si la imagen ha sido borrada o no
     * @param path: String,imagesIndex:List<Int> path de donde están las imagenes y el index que ocupa dicha imagen
     * */
    override suspend fun getLocalPlantImage(path: String): List<Bitmap> = LocalStorageService().getLocalImage(path)

    @RequiresApi(Build.VERSION_CODES.O)
    /**
     * Propósito: borra imagenes del almacenamiento local llamando al servicio
     * @return Boolean si la imagen ha sido borrada o no
     * @param path: String,imagesIndex:List<Int> path de donde están las imagenes y el index que ocupa dicha imagen
     * */
    override suspend fun deleteImage(path: String, imagesIndex:List<Int>): Boolean = LocalStorageService().deleteLocalImage(path,imagesIndex)

}