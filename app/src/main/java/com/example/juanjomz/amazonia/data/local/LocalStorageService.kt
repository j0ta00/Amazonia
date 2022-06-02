package com.example.juanjomz.amazonia.data.local

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.pathString

class LocalStorageService {
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
}