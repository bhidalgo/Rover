package com.example.rover.dal.api.model

import com.example.rover.model.RoverPhoto
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RoverPhotosResponse(
    @SerializedName("photos")
    @Expose
    val photos: ArrayList<RoverPhoto>
)