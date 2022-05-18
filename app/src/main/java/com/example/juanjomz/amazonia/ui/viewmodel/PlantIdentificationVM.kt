package com.example.juanjomz.amazonia.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juanjomz.amazonia.data.remote.datasource.PlantRemoteDataSourceImpl
import com.example.juanjomz.amazonia.data.repository.PlantRepository
import com.example.juanjomz.amazonia.domain.PlantBO
import com.example.juanjomz.amazonia.usecases.GetPlantUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class PlantIdentificationVM : ViewModel(){
    private val getPlantUseCase = GetPlantUseCase(PlantRepository(PlantRemoteDataSourceImpl()))
    private val _plant = MutableLiveData<PlantBO>()
    val plant: LiveData<PlantBO> get() = _plant



    fun loadPlant(photo: File, organ:String){


        //dispatcher io para llamadas largas a apis y cosas así
            _plant.postValue(getPlantUseCase.invoke(photo,organ))

    }

}