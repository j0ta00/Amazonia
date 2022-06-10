package com.example.juanjomz.amazonia.data.local

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.pathString

class LocalStorageService {
    /**
     * Propósito: obtiene las imagenes almacenadas en el almacenamiento local
     * @return List<Bitmap> listado de imagenes
     * @param path: String el path de donde se encuentran dichas imágenes
     * */
    private val imagesList: LinkedList<Bitmap> = LinkedList()
    @RequiresApi(Build.VERSION_CODES.O)
    fun getLocalImage(path:String):List<Bitmap>{
        if(File(path).exists()) {
            Files.walk(Paths.get(path)).forEach {
                val image = BitmapFactory.decodeFile(it.pathString)
                if (image != null) {
                    imagesList.add(image)
                }
            }
        }
        return imagesList
    }
    /**
     * Propósito: borra imagenes del almacenamiento local
     * @return Boolean si la imagen ha sido borrada o no
     * @param path: String,imagesIndex:List<Int> path de donde están las imagenes y el index que ocupa dicha imagen
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteLocalImage(path:String, imagesIndex:List<Int>):Boolean{
        var counter=0
        val listToDelete= mutableListOf<File>()
        val imagesPath=Files.walk(Paths.get(path)).toArray()
        imagesIndex.forEach{
          if(File(imagesPath[it+1].toString()).exists()){
              listToDelete.add(File(Files.walk(Paths.get(path)).toArray()[it+1].toString()))
              counter++
          }
        }
        listToDelete.forEach{it.delete()}
        return counter==imagesIndex.size
    }
}