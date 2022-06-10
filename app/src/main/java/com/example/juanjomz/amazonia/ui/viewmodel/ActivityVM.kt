package com.example.juanjomz.amazonia.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
/**
 * Viewmodel de la actividad, encargado de refrescar las vistas en función de que necesiten actualizarse
 * @author jjmza
 * */
class ActivityVM: ViewModel() {
    val refreshImages: LiveData<Boolean> get() =_refreshImages
    private val _refreshImages = MutableLiveData<Boolean>()
    val refreshSpecies: LiveData<Boolean> get() = _refreshSpecies
    private val _refreshSpecies = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean> get() = _showDialog
    private val _showDialog = MutableLiveData<Boolean>()
    /**
     * Proposito:Indica si se deben o no refrescar la vista del listado de especies
     * @param refresh:Boolean
     * */
    fun refreshSpecies(refresh:Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            _refreshSpecies.postValue(refresh)
        }
    }
    /**
     * Proposito:Indica si se deben o no refrescar la vista del la galería
     * @param refresh:Boolean
     * */
    fun refreshImages(refresh:Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            _refreshImages.postValue(refresh)
        }
    }
    /**
     * Proposito:Indica si se deben mostrar o no los dialogs de identificación de la planta
     * @param refresh:Boolean
     * */
    fun changeShowDialog(refresh:Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            _showDialog.postValue(refresh)
        }
    }
}