package com.example.rover.main.viewmodel

import android.app.Application
import android.net.ConnectivityManager
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.rover.dal.api.CURIOSITY
import com.example.rover.dal.api.Rover
import com.example.rover.dal.repository.ApiRequestLimitReachedException
import com.example.rover.dal.repository.RoverPhotoRepository
import com.example.rover.model.RoverPhoto
import kotlinx.coroutines.*
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

    val apiRequestsErrorVisibility = MutableLiveData<Int>().apply {
        View.GONE
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Default + Job()
    lateinit var roverPhotoRepository: RoverPhotoRepository
    lateinit var connectivityManager: ConnectivityManager

    fun fetchRoverPhotos(@Rover rover: String = currentRover, sol: Int, blockScreen: Boolean = true) = launch {
        if (blockScreen) {
            blockScreenToLoad()
        }

        val fetchedPhotos = try {
            roverPhotoRepository.getRoverPhotos(rover, sol)
        } catch (e: ApiRequestLimitReachedException) {
            displayTooManyRequests()
            return@launch
        }

        if (fetchedPhotos == null && connectivityManager.activeNetworkInfo?.isConnected != true && roverPhotos.value == null) {
            displayPersistedPhotos()
        } else if (fetchedPhotos == null && roverPhotos.value != null) {
            allowUserToInteractWithPersistedData()
        } else if (fetchedPhotos != null) {
            currentSol = sol
            val currentRoverPhotos = roverPhotos.value ?: ArrayList()
            currentRoverPhotos.addAll(fetchedPhotos)
            roverPhotos.postValue(currentRoverPhotos)
            allowUserToInteractWithPersistedData()
        }
    }

    fun fetchMostRecentRoverPhotos(@Rover rover: String = currentRover, blockScreen: Boolean = true) = launch {
        if (blockScreen) {
            blockScreenToLoad()
        }

        currentRover = rover

        val maxSol = try {
            roverPhotoRepository.getRoverManifest(rover)?.maxSol
        } catch (e: ApiRequestLimitReachedException) {
            displayTooManyRequests()
            return@launch
        }

        if (maxSol == null && connectivityManager.activeNetworkInfo?.isConnected != true && roverPhotos.value.isNullOrEmpty()) {
            displayPersistedPhotos()
        } else if (maxSol == null && !roverPhotos.value.isNullOrEmpty()) {
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
        apiRequestsErrorVisibility.postValue(View.GONE)
    }

    private fun allowUserToInteractWithPersistedData() {
        networkErrorVisibility.postValue(View.GONE)
        contentVisibility.postValue(View.VISIBLE)
        loadingProgressBarVisibility.postValue(View.GONE)
        roverPhotosVisibility.postValue(View.VISIBLE)
        apiRequestsErrorVisibility.postValue(View.GONE)
    }

    private fun displayTooManyRequests() {
        if (roverPhotos.value.isNullOrEmpty()) {
            networkErrorVisibility.postValue(View.GONE)
            contentVisibility.postValue(View.GONE)
            loadingProgressBarVisibility.postValue(View.GONE)
            roverPhotosVisibility.postValue(View.GONE)
            apiRequestsErrorVisibility.postValue(View.VISIBLE)
        } else {
            runBlocking(Dispatchers.Main) {
                Toast.makeText(getApplication(), "API Request limit reached", Toast.LENGTH_LONG).show()
            }
        }
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