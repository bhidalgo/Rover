package com.example.rover.dagger

import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import androidx.room.Room
import com.example.rover.api.MARS_ROVER_API_BASE_URL
import com.example.rover.api.MarsRoverApi
import com.example.rover.api.repository.RoverPhotoRepository
import com.example.rover.application.RoverApplication
import com.example.rover.room.DATABASE_NAME
import com.example.rover.room.RoverPhotoDatabase
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class RoverModule {
    @Provides
    @Singleton
    fun provideMarsRoverApi(): MarsRoverApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(MARS_ROVER_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(MarsRoverApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRoverPhotoRepository(connectivityManager: ConnectivityManager, marsRoverApi: MarsRoverApi, roverPhotoDatabase: RoverPhotoDatabase) = RoverPhotoRepository(connectivityManager, marsRoverApi, roverPhotoDatabase)

    @Provides
    @Singleton
    fun provideRoverPhotoDatabase(application: RoverApplication): RoverPhotoDatabase {
        return Room.databaseBuilder(application, RoverPhotoDatabase::class.java, DATABASE_NAME).build()
    }

    @Provides
    @Singleton
    fun provideConnectivityManager(application: RoverApplication): ConnectivityManager {
        return application.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    }
}