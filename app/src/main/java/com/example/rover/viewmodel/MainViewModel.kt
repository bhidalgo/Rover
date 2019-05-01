package com.example.rover.viewmodel

import android.app.Application
import android.net.ConnectivityManager
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.rover.api.CURIOSITY
import com.example.rover.api.Rover
import com.example.rover.api.repository.RoverPhotoRepository
import com.example.rover.model.RoverPhoto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {
    var currentSol = -1

    @Rover
    var currentRover: String = CURIOSITY

    val roverPhotos = MutableLiveData<ArrayList<RoverPhoto>>()

    val roverPhotosVisibility = MutableLiveData<Int>().apply {
        value = View.GONE
    }

    val loadingProgressBarVisibility = MutableLiveData<Int>().apply {
        View.VISIBLE
    }

    val contentVisibility = MutableLiveData<Int>().apply {
        View.VISIBLE
    }

    val networkErrorVisibility = MutableLiveData<Int>().apply {
        View.GONE
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Default + Job()
    lateinit var roverPhotoRepository: RoverPhotoRepository
    lateinit var connectivityManager: ConnectivityManager

    fun fetchRoverPhotos(@Rover rover: String = currentRover, sol: Int, blockScreen: Boolean = true) = launch {
        if(blockScreen) {
            blockScreenToLoad()
        }

        val fetchedPhotos = roverPhotoRepository.getRoverPhotos(rover, sol)
        if (fetchedPhotos == null && connectivityManager.activeNetworkInfo?.isConnected != true && roverPhotos.value == null) {
            displayPersistedPhotos()
        } else if (fetchedPhotos == null && roverPhotos.value != null) {
            allowUserToInteractWithPersistedData()
        } else if (fetchedPhotos != null) {
            currentSol = sol
            roverPhotos.postValue(fetchedPhotos)
            allowUserToInteractWithPersistedData()
        }
    }

    fun fetchMostRecentRoverPhotos(@Rover rover: String = currentRover, blockScreen: Boolean = true) = launch {
        if(blockScreen) {
            blockScreenToLoad()
        }

        val maxSol = roverPhotoRepository.getRoverManifest(rover)?.maxSol
        if (maxSol == null && connectivityManager.activeNetworkInfo?.isConnected != true && roverPhotos.value == null) {
            displayPersistedPhotos()
        } else if (maxSol == null && roverPhotos.value != null) {
            allowUserToInteractWithPersistedData()
        } else if (maxSol != null) {
            currentSol = maxSol
            fetchRoverPhotos(rover, maxSol, false).join()
            allowUserToInteractWithPersistedData()
        }
    }

    private fun blockScreenToLoad() {
        networkErrorVisibility.postValue(View.GONE)
        contentVisibility.postValue(View.VISIBLE)
        roverPhotosVisibility.postValue(View.GONE)
        loadingProgressBarVisibility.postValue(View.VISIBLE)
    }

    private fun allowUserToInteractWithPersistedData() {
        networkErrorVisibility.postValue(View.GONE)
        contentVisibility.postValue(View.VISIBLE)
        loadingProgressBarVisibility.postValue(View.GONE)
        roverPhotosVisibility.postValue(View.VISIBLE)
    }

    private suspend fun displayPersistedPhotos() {
        val persistedPhotos = roverPhotoRepository.getPersistedPhotos(currentRover)

        if (persistedPhotos.isNullOrEmpty()) {
            contentVisibility.postValue(View.GONE)
            networkErrorVisibility.postValue(View.VISIBLE)
        } else {
            contentVisibility.postValue(View.VISIBLE)
            networkErrorVisibility.postValue(View.GONE)
            roverPhotos.postValue(persistedPhotos)
        }
    }
}