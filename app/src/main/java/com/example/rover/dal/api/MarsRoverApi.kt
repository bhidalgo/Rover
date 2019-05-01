package com.example.rover.dal.api

import androidx.annotation.StringDef
import com.example.rover.dal.api.model.RoverManifestResponse
import com.example.rover.dal.api.model.RoverPhotosResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

@StringDef(CURIOSITY, OPPORTUNITY, SPIRIT)
@Retention(AnnotationRetention.SOURCE)
annotation class Rover

const val CURIOSITY = "curiosity"
const val OPPORTUNITY = "opportunity"
const val SPIRIT = "spirit"

const val MARS_ROVER_API_BASE_URL = "https://api.nasa.gov/"

interface MarsRoverApi {
    @GET("mars-photos/api/v1/rovers/{rover}/photos")
    fun getRoverPhotos(
        @Rover
        @Path("rover")
        rover: String,
        @Query("sol")
        sol: Int,
        @Query("api_key")
        apiKey: String = "DEMO_KEY",
        @Query("page")
        page: Int = 1
    ): Call<RoverPhotosResponse>

    @GET("mars-photos/api/v1/manifests/{rover}")
    fun getRoverManifest(
        @Rover
        @Path("rover")
        rover: String,
        @Query("api_key")
        apiKey: String = "DEMO_KEY"
    ): Call<RoverManifestResponse>
}