package com.example.rover.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

const val PHOTO_TABLE_NAME = "photos"
@Entity(tableName = PHOTO_TABLE_NAME)
data class RoverPhoto(
    @PrimaryKey
    @SerializedName("id")
    @Expose
    val id: Int,

    @ColumnInfo(name = "sol")
    @SerializedName("sol")
    @Expose
    val sol: Int,

    @ColumnInfo(name = "camera")
    @SerializedName("camera")
    @Expose
    val camera: RoverCamera,

    @ColumnInfo(name = "img_src")
    @SerializedName("img_src")
    @Expose
    val imgSrc: String,

    @ColumnInfo(name = "earth_date")
    @SerializedName("earth_date")
    @Expose
    val earthDate: String,

    @ColumnInfo(name = "rover")
    @SerializedName("rover")
    @Expose
    val rover: RoverData
)