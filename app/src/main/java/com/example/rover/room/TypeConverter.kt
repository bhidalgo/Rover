package com.example.rover.room

import androidx.room.TypeConverter
import com.example.rover.model.RoverCamera
import com.example.rover.model.RoverData

object TypeConverter {
    @JvmStatic
    @TypeConverter
    fun roverCameraToStorable(camera: RoverCamera): String {
        with(camera) {
            return "$cameraId:/C/:$cameraName:/C/:$roverId:/C/:$cameraFullName"
        }
    }

    @JvmStatic
    @TypeConverter
    fun roverCameraFromStorable(storableString: String): RoverCamera {
        val indexed = storableString.split(":/C/:")
        return RoverCamera(
            cameraId = indexed[0].trim().toInt(),
            cameraName = indexed[1].trim(),
            roverId = indexed[2].trim().toInt(),
            cameraFullName = indexed[3].trim()
        )
    }

    @JvmStatic
    @TypeConverter
    fun roverDataToStorable(data: RoverData): String {
        with(data) {
            return "$roverId:/D/:$roverName:/D/:$landingDate:/D/:$launchDate:/D/:$status:/D/:$maxSol:/D/:$maxDate:/D/:$totalPhotos:/D/:${cameras.joinToString(":/CL/:")}"
        }
    }

    @JvmStatic
    @TypeConverter
    fun roverDataFromStorable(storableString: String): RoverData {
        val indexed = storableString.split(":/D/:")

        return RoverData(
            roverId = indexed[0].trim().toInt(),
            roverName = indexed[1].trim(),
            landingDate = indexed[2].trim(),
            launchDate = indexed[3].trim(),
            status = indexed[4].trim(),
            maxSol = indexed[5].trim().toInt(),
            maxDate = indexed[6].trim(),
            totalPhotos = indexed[7].trim().toInt(),
            cameras = run {
                val restoredCameras = ArrayList<RoverCamera>()
                indexed[8].split(":/CL/:").forEach {
                    roverCameraFromStorable(it)
                }
                return@run restoredCameras
            }
        )
    }
}