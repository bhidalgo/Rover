package com.example.rover.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.rover.model.RoverPhoto

const val DATABASE_NAME = "rover_photo_db"
@Database(entities = [RoverPhoto::class], version = 1, exportSchema = false)
@TypeConverters(TypeConverter::class)
abstract class RoverPhotoDatabase : RoomDatabase() {
    abstract fun roverPhotoDao(): RoverPhotoDao
}