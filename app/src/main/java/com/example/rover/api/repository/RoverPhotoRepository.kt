package com.example.rover.api.repository

import com.example.rover.api.MarsRoverApi
import com.example.rover.model.RoverPhoto
import com.example.rover.room.RoverPhotoDatabase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoverPhotoRepository @Inject constructor(private val marsRoverApi: MarsRoverApi, private val roverPhotoDatabase: RoverPhotoDatabase) {
    suspend fun getRoverPhotos(rover: String, sol: Int): List<RoverPhoto> = coroutineScope {
        roverPhotoDatabase.roverPhotoDao().getAll().takeUnless { it.isEmpty() }?.let {
            return@coroutineScope it
        }

        val response = withContext(this.coroutineContext) {
            marsRoverApi.getRoverPhotos(rover, sol).execute()
        }

        val responseBody = response.body()

        return@coroutineScope when {
            response.isSuccessful && responseBody != null -> {
                roverPhotoDatabase.roverPhotoDao().insertAll(responseBody.photos)
                responseBody.photos
            }
            else -> TODO("Not yet implemented.")
        }
    }
}