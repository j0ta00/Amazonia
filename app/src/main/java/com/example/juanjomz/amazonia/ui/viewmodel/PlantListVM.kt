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
import com.example.juanjomz.amazonia.usecases.GetPlantUseCase
import com.example.juanjomz.amazonia.usecases.GetSpeciesImagesUseCase
import com.example.juanjomz.amazonia.usecases.GetSpeciesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
/**
 * Viewmodel de la actividad, encargado de conectar la vista con el repositorio y permitir las funcionalidades en segundo plano y la carga de datos
 * @author jjmza
 * */
    class PlantListVM : ViewModel() {
    private val _imageList = MutableLiveData<List<String>>()
    val imageList: LiveData<List<String>> get() = _imageList
    private val _speciesList = MutableLiveData<List<PlantBO>>()
    val speciesList: LiveData<List<PlantBO>> get() = _speciesList
    private val getSpeciesImagesUseCase = GetSpeciesImagesUseCase(PlantImageRepository(PlantImageRemoteDataSourceImpl()))
    private val getSpeciesUseCase = GetSpeciesUseCase(PlantRepository(PlantRemoteDataSourceImpl()))
    /**
     *
     * Proposito:Llama en un hilo secundario a las funciones necesarias para obtener el listado de las imágenes
     * @param species:List<PlantBO>
     * */
    fun loadListOfImages(species:List<PlantBO>){
        viewModelScope.launch(Dispatchers.IO) {
            _imageList.postValue(getSpeciesImagesUseCase.invoke(species))
        }
    }
    /**
     *
     * Proposito:Llama en un hilo secundario a las funciones necesarias para obtener el listado de las especies
     * @param email:String
     * */
    fun loadSpeciesList(email:String){
        viewModelScope.launch(Dispatchers.IO) {
            _speciesList.postValue(getSpeciesUseCase.invoke(email))
        }
    }

}