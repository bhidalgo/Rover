package com.example.rover.api.model

import com.example.rover.model.RoverManifest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RoverManifestResponse(
    @SerializedName("photo_manifest")
    @Expose
    val manifest: RoverManifest
)