package com.example.rover.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.rover.api.Rover
import com.example.rover.model.MANIFEST_TABLE_NAME
import com.example.rover.model.PHOTO_TABLE_NAME
import com.example.rover.model.RoverManifest
import com.example.rover.model.RoverPhoto

@Dao
interface RoverPhotoDao {
    @Query("SELECT * FROM $PHOTO_TABLE_NAME")
    fun getAllPhotos(): List<RoverPhoto>

    @Query("SELECT * FROM $PHOTO_TABLE_NAME WHERE rover LIKE  '%' || :rover || '%' AND sol = :sol")
    fun getRoverPhotosBySol(@Rover rover: String, sol: Int): List<RoverPhoto>

    @Query("SELECT * FROM $MANIFEST_TABLE_NAME WHERE name LIKE :rover LIMIT 1")
    fun getRoverManifest(@Rover rover: String): RoverManifest?

    @Insert(onConflict = REPLACE)
    fun insertManifest(manifest: RoverManifest)

    @Insert(onConflict = REPLACE)
    fun insertAllPhotos(photos: List<RoverPhoto>)
}