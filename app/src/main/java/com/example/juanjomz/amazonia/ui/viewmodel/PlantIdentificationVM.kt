package com.example.juanjomz.amazonia.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juanjomz.amazonia.data.remote.datasource.PlantImageRemoteDataSourceImpl
import com.example.juanjomz.amazonia.data.remote.datasource.PlantRemoteDataSourceImpl
import com.example.juanjomz.amazonia.data.repository.PlantImageRepository
import com.example.juanjomz.amazonia.data.repository.PlantRepository
import com.example.juanjomz.amazonia.domain.PlantBO
import com.example.juanjomz.amazonia.usecases.AddPlantUseCase
import com.example.juanjomz.amazonia.usecases.GetPlantImageUseCase
import com.example.juanjomz.amazonia.usecases.GetPlantUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PlantIdentificationVM : ViewModel(){
    private val getPlantUseCase = GetPlantUseCase(PlantRepository(PlantRemoteDataSourceImpl()))
    private val _plant = MutableLiveData<List<PlantBO>>()
    val plant: LiveData<List<PlantBO>> get() = _plant
    private val addPlant = AddPlantUseCase(PlantRepository(PlantRemoteDataSourceImpl()))
    private val getPlantImage = GetPlantImageUseCase(PlantImageRepository(PlantImageRemoteDataSourceImpl()))
    private val _image = MutableLiveData<List<String>>()
    val image: LiveData<List<String>> get() = _image
    private val _specieAdded = MutableLiveData<Boolean>()
    val specieAdded: LiveData<Boolean> get() = _specieAdded

    fun loadPlant( requestbody: RequestBody){
        viewModelScope.launch(Dispatchers.IO) {
            //dispatcher io para llamadas largas a apis y cosas así
            _plant.postValue(getPlantUseCase.invoke(requestbody))
        }
    }
    fun addPlant(email:String,plant:PlantBO){
    viewModelScope.launch(Dispatchers.IO) {
        _specieAdded.postValue(addPlant.invoke(email,plant))
    }
}
    fun searchImage(plantName: String){
        viewModelScope.launch(Dispatchers.IO) {
            _image.postValue(getPlantImage.invoke(plantName))
        }
    }

}