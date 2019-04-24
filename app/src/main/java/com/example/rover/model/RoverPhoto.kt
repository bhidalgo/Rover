package com.example.rover.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RoverPhoto(
    @SerializedName("id")
    @Expose
    val id: Int,

    @SerializedName("sol")
    @Expose
    val sol: Int,

    @SerializedName("camera")
    @Expose
    val camera: RoverCamera,

    @SerializedName("img_src")
    @Expose
    val imgSrc: String,

    @SerializedName("earth_date")
    @Expose
    val earthDate: String,

    @SerializedName("rover")
    @Expose
    val rover: RoverData
)