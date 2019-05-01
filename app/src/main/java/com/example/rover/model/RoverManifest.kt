package com.example.rover.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rover.api.CURIOSITY
import com.example.rover.api.OPPORTUNITY
import com.example.rover.api.SPIRIT
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

const val MANIFEST_TABLE_NAME = "manifest_table"
@Entity(tableName = MANIFEST_TABLE_NAME)
data class RoverManifest(
    @ColumnInfo(name = "name")
    @SerializedName("name")
    @Expose
    val roverName: String,

    @ColumnInfo(name = "max_sol")
    @SerializedName("max_sol")
    @Expose
    val maxSol: Int,

    @PrimaryKey(autoGenerate = true)
    val id: Int
)