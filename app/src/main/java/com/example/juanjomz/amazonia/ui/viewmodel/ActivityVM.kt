package com.example.juanjomz.amazonia.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActivityVM: ViewModel() {
    val refreshImages: LiveData<Boolean> get() =_refreshImages
    private val _refreshImages = MutableLiveData<Boolean>()
    val refreshSpecies: LiveData<Boolean> get() = _refreshSpecies
    private val _refreshSpecies = MutableLiveData<Boolean>()


    fun refreshSpecies(refresh:Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            _refreshSpecies.postValue(refresh)
        }
    }
    fun refreshImages(refresh:Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            _refreshImages.postValue(refresh)
        }
    }
}