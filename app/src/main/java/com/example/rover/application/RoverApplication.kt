package com.example.rover.application

import android.app.Application
import com.example.rover.dagger.DaggerRoverComponent
import com.example.rover.dagger.RoverComponent

open class RoverApplication : Application() {
    lateinit var roverComponent: RoverComponent

    override fun onCreate() {
        super.onCreate()
        roverComponent = DaggerRoverComponent.builder().application(this).build()
    }
}