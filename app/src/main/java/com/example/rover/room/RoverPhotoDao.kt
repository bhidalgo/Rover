package com.example.rover.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.rover.model.PHOTO_TABLE_NAME
import com.example.rover.model.RoverPhoto

@Dao
interface RoverPhotoDao {
    @Query("SELECT * FROM $PHOTO_TABLE_NAME")
    fun getAll(): List<RoverPhoto>

    @Query("SELECT * FROM $PHOTO_TABLE_NAME WHERE rover LIKE :rover AND sol LIKE :sol")
    fun getPhotos(rover: String, sol: Int): List<RoverPhoto>

    @Insert(onConflict = REPLACE)
    fun insertAll(photos: List<RoverPhoto>)
}