package com.example.juanjomz.amazonia.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juanjomz.amazonia.data.local.datasource.PlantImageLocalDataSourceImpl
import com.example.juanjomz.amazonia.data.remote.datasource.PlantImageRemoteDataSourceImpl
import com.example.juanjomz.amazonia.data.remote.datasource.PlantRemoteDataSourceImpl
import com.example.juanjomz.amazonia.data.repository.LocalImagesRepository
import com.example.juanjomz.amazonia.data.repository.PlantImageRepository
import com.example.juanjomz.amazonia.data.repository.PlantRepository
import com.example.juanjomz.amazonia.domain.PlantBO
import com.example.juanjomz.amazonia.usecases.DeleteImageUseCase
import com.example.juanjomz.amazonia.usecases.GetLocalImagesUseCase
import com.example.juanjomz.amazonia.usecases.GetSpeciesImagesUseCase
import com.example.juanjomz.amazonia.usecases.GetSpeciesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GalleryVM : ViewModel() {
    private val _imageList = MutableLiveData<List<Bitmap>>()
    val imageList: LiveData<List<Bitmap>> get() = _imageList
    private val _imagesDeleted=MutableLiveData<Boolean>()
    val imagesDeleted:LiveData<Boolean> get()=_imagesDeleted
    private val getLocalImagesUseCase = GetLocalImagesUseCase(LocalImagesRepository(PlantImageLocalDataSourceImpl()))
    private val getDeleteImagesUseCase = DeleteImageUseCase(LocalImagesRepository(PlantImageLocalDataSourceImpl()))
    fun deleteImages(path:String,imagesIndex:List<Int>){
        viewModelScope.launch(Dispatchers.IO) {
            _imagesDeleted.postValue(getDeleteImagesUseCase.invoke(path,imagesIndex))
        }
    }


    fun loadLocalStorageImages(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _imageList.postValue(getLocalImagesUseCase.invoke(path))
        }
    }
}