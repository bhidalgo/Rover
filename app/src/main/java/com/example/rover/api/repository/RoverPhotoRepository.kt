package com.example.rover.api.repository

import android.net.ConnectivityManager
import android.util.Log
import com.example.rover.api.MarsRoverApi
import com.example.rover.api.Rover
import com.example.rover.model.RoverManifest
import com.example.rover.model.RoverPhoto
import com.example.rover.room.RoverPhotoDatabase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class RoverPhotoRepository @Inject constructor(private val connectivityManager: ConnectivityManager, private val marsRoverApi: MarsRoverApi, private val roverPhotoDatabase: RoverPhotoDatabase) {
    suspend fun getRoverManifest(@Rover rover: String): RoverManifest? = coroutineScope {
        roverPhotoDatabase.roverPhotoDao().getRoverManifest(rover).takeUnless { it == null }?.let {
            return@coroutineScope it
        }

        if(!isConnectedToNetwork()) {
            return@coroutineScope null
        }

        val response = runCatching {
            marsRoverApi.getRoverManifest(rover).execute()
        }.getOrNull()

        val responseBody = response?.body()

        return@coroutineScope when {
            response != null && response.isSuccessful && responseBody != null -> {
                roverPhotoDatabase.roverPhotoDao().insertManifest(responseBody.manifest)
                responseBody.manifest
            }
            response?.code() == 429 -> throw ApiRequestLimitReachedException()
            else -> {
                Log.e("RoverPhotoRepository", response?.message() ?: "Could not fetch $rover\'s manifest.")
                null
            }
        }
    }

    suspend fun getRoverPhotos(@Rover rover: String, sol: Int): ArrayList<RoverPhoto>? = coroutineScope {
        roverPhotoDatabase.roverPhotoDao().getRoverPhotosBySol(rover, sol).takeUnless { it.isEmpty() }?.let {
            return@coroutineScope ArrayList<RoverPhoto>().apply {
                addAll(it)
            }
        }

        if(!isConnectedToNetwork()) {
            return@coroutineScope null
        }

        val response = runCatching {
            marsRoverApi.getRoverPhotos(rover, sol).execute()
        }.getOrNull()

        val responseBody = response?.body()

        return@coroutineScope when {
            response != null && response.isSuccessful && responseBody != null -> {
                prefetchAndCachePhotos(responseBody.photos)
                roverPhotoDatabase.roverPhotoDao().insertAllPhotos(responseBody.photos)
                responseBody.photos
            }
            response?.code() == 429 -> throw ApiRequestLimitReachedException()
            else -> {
                Log.e("RoverPhotoRepository", response?.message() ?: "Could not fetch $rover\'s photos.")
                null
            }
        }
    }

    suspend fun getPersistedPhotos(@Rover rover: String): ArrayList<RoverPhoto>? = coroutineScope {
       return@coroutineScope ArrayList<RoverPhoto>().apply {
           addAll(roverPhotoDatabase.roverPhotoDao().getRoverPhotos(rover))
       }
    }

    private fun isConnectedToNetwork(): Boolean = connectivityManager.activeNetworkInfo?.isConnected == true

    private fun prefetchAndCachePhotos(photos: List<RoverPhoto>) {
        val picasso = Picasso.get()
        for (photo in photos) {
            picasso.load(photo.imgSrc)
        }
    }
}

class ApiRequestLimitReachedException : Exception()