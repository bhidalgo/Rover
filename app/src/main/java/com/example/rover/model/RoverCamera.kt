package com.example.rover.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RoverCamera(
    @SerializedName("id")
    @Expose
    val cameraId: Int = -1,

    @SerializedName("name")
    @Expose
    val cameraName: String,

    @SerializedName("rover_id")
    @Expose
    val roverId: Int = -1,

    @SerializedName("full_name")
    @Expose
    val cameraFullName: String
)