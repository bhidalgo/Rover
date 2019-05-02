package com.example.rover.dagger

import android.net.ConnectivityManager
import com.example.rover.dal.api.MarsRoverApi
import com.example.rover.dal.repository.RoverPhotoRepository
import com.example.rover.dal.room.RoverPhotoDatabase
import dagger.Module
import dagger.Provides
import org.mockito.Mockito.mock
import javax.inject.Singleton

@Module
class TestRoverModule {
    @Provides
    @Singleton
    fun provideMarsRoverApi(): MarsRoverApi = mock(MarsRoverApi::class.java)

    @Provides
    @Singleton
    fun provideRoverPhotoRepository(): RoverPhotoRepository = mock(RoverPhotoRepository::class.java)

    @Provides
    @Singleton
    fun provideRoverPhotoDatabase(): RoverPhotoDatabase = mock(RoverPhotoDatabase::class.java)

    @Provides
    @Singleton
    fun provideConnectivityManager(): ConnectivityManager = mock(ConnectivityManager::class.java)
}