package com.example.rover.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.rover.api.CURIOSITY
import com.example.rover.api.repository.RoverPhotoRepository
import com.example.rover.model.RoverPhoto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {
    val roverPhotos = MutableLiveData<List<RoverPhoto>>()

    val roverPhotosVisibility = MutableLiveData<Int>().apply {
        value = View.GONE
    }

    val loadingProgressBarVisibility = MutableLiveData<Int>().apply {
        View.VISIBLE
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Default + Job()
    lateinit var roverPhotoRepository: RoverPhotoRepository

    fun fetchRoverPhotos(rover: String = CURIOSITY) = launch {
        roverPhotosVisibility.postValue(View.GONE)
        loadingProgressBarVisibility.postValue(View.VISIBLE)
        roverPhotos.postValue(roverPhotoRepository.getRoverPhotos(rover, 1000))
        loadingProgressBarVisibility.postValue(View.GONE)
        roverPhotosVisibility.postValue(View.VISIBLE)
    }
}