package com.example.rover.model

import androidx.annotation.StringDef
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@StringDef(ACTIVE, INACTIVE)
@Retention(AnnotationRetention.SOURCE)
annotation class RoverStatus

const val ACTIVE = "active"
const val INACTIVE = "inactive"

data class RoverData(
    @SerializedName("id")
    @Expose
    val roverId: Int,

    @SerializedName("name")
    @Expose
    val roverName: String,

    @SerializedName("landing_date")
    @Expose
    val landingDate: String,

    @SerializedName("launch_date")
    @Expose
    val launchDate: String,

    @SerializedName("status")
    @Expose
    @RoverStatus
    val status: String,

    @SerializedName("max_sol")
    @Expose
    val maxSol: Int,

    @SerializedName("max_date")
    @Expose
    val maxDate: String,

    @SerializedName("total_photos")
    @Expose
    val totalPhotos: Int,

    @SerializedName("cameras")
    @Expose
    val cameras: List<RoverCamera>
)