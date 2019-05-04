package com.example.rover.dal.repository

import android.net.ConnectivityManager
import android.util.Log
import com.example.rover.dal.api.MarsRoverApi
import com.example.rover.dal.api.Rover
import com.example.rover.dal.exception.ApiRequestLimitReachedException
import com.example.rover.dal.room.RoverPhotoDatabase
import com.example.rover.model.RoverManifest
import com.example.rover.model.RoverPhoto
import com.example.rover.util.isConnectedToNetwork
import com.squareup.picasso.Picasso
import kotlinx.coroutines.coroutineScope
import retrofit2.Response
import javax.inject.Inject

const val API_REQUEST_LIMIT_CODE = 429

private const val TAG = "RoverPhotoRepository"

class RoverPhotoRepository @Inject constructor(private val connectivityManager: ConnectivityManager, private val marsRoverApi: MarsRoverApi, private val roverPhotoDatabase: RoverPhotoDatabase) {

    /**
     * Retrieves a specific Rover manifest.
     *
     * If the manifest is available via the database then it will immediately return the database's record.
     * Otherwise, if the user has connection, it will request that manifest from the API.
     *
     * @param rover - The rover manifest to request.
     * @return - (Nullable) RoverManifest
     */
    suspend fun getRoverManifest(@Rover rover: String): RoverManifest? = coroutineScope {
        val databaseValue = roverPhotoDatabase.roverPhotoDao().getRoverManifest(rover)

        return@coroutineScope when {
            databaseValue != null -> databaseValue
            connectivityManager.isConnectedToNetwork() -> {
                //Request from the API, obscuring any exceptions thrown by simply returning null.
                val response = runCatching {
                    marsRoverApi.getRoverManifest(rover).execute()
                }.getOrNull()

                //Only take a manifest if the response returned successful.
                val receivedManifest = response?.body()?.manifest?.takeIf { response.isSuccessful }

                //If the manifest wasn't accepted then check for request limit error code.
                storeInDatabase(receivedManifest) ?: checkApiRequestLimit(response)
            }
            else -> null
        }
    }

    /**
     * Retrieves a list of Rover photos on a specific Martian Sol.
     *
     * If the list for that Sol is available via the database then it will immediately return the database's record.
     * Otherwise, if the user has connection, it will requuest that manifest from the API.
     *
     * @param rover - The rover who's photos will be fetched.
     * @param sol - The specific Martian Sol to search for.
     * @return - (Nullable) ArrayList<RoverPhoto>
     */
    suspend fun getRoverPhotos(@Rover rover: String, sol: Int): ArrayList<RoverPhoto>? = coroutineScope {
        val databaseValue = roverPhotoDatabase.roverPhotoDao().getRoverPhotosBySol(rover, sol)

        return@coroutineScope when {
            !databaseValue.isNullOrEmpty() -> {
                ArrayList<RoverPhoto>().apply {
                    addAll(databaseValue)
                }
            }
            connectivityManager.isConnectedToNetwork() -> {
                val response = kotlin.runCatching {
                    marsRoverApi.getRoverPhotos(rover, sol).execute()
                }.getOrNull()

                val receivedPhotos = response?.body()?.photos?.takeIf { response.isSuccessful }

                storeInDataBase(receivedPhotos) ?: checkApiRequestLimit(response)
            }
            else -> null
        }
    }

    /**
     * Returns a list of photos for a specific Rover within the database.
     *
     * @param rover - The rover who's photos will be fetched.
     * @return - (Nullable) ArrayList<RoverPhoto>
     */
    suspend fun getPersistedPhotos(@Rover rover: String): ArrayList<RoverPhoto>? = coroutineScope {
       return@coroutineScope ArrayList<RoverPhoto>().apply {
           addAll(roverPhotoDatabase.roverPhotoDao().getRoverPhotos(rover))
       }
    }

    /**
     * Sets the Picasso instance to load a list of photos so that it may cache them for later use.
     *
     * @param photos - A list of photos to cache.
     */
    private fun prefetchAndCachePhotos(photos: List<RoverPhoto>) {
        val picasso = Picasso.get()
        for (photo in photos) {
            picasso.load(photo.imgSrc)
        }
    }

    /**
     * Checks against the response to see if the request limit has been reached.
     *
     * @param response - The response to check against.
     * @return - Nothing
     * @throws ApiRequestLimitReachedException
     */
    private fun checkApiRequestLimit(response: Response<*>?): Nothing? {
        return if(response?.code() == API_REQUEST_LIMIT_CODE) {
            throw ApiRequestLimitReachedException()
        } else {
            Log.e(TAG, response?.message() ?: "API request unsuccessful.")
            null
        }
    }

    /**
     * Stores a manifest within the database if it is not null.
     *
     * @param manifest - The manifest to store.
     * @return RoverManifest.
     */
    private fun storeInDatabase(manifest: RoverManifest?): RoverManifest? {
        return manifest?.let {
            roverPhotoDatabase.roverPhotoDao().insertManifest(manifest)
            manifest
        }
    }

    /**
     * Stores a list of photos within the database if it is not null.
     * The photos are also fetched and cached.
     *
     * @param roverPhotos - A list of rover photos to store
     * @return - (Nullable) ArrayList<RoverPhoto>
     */
    private fun storeInDataBase(roverPhotos: ArrayList<RoverPhoto>?): ArrayList<RoverPhoto>? {
        return roverPhotos?.let {
            prefetchAndCachePhotos(roverPhotos)
            roverPhotoDatabase.roverPhotoDao().insertAllPhotos(roverPhotos)
            roverPhotos
        }
    }
}

