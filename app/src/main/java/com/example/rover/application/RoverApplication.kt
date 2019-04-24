package com.example.rover.application

import android.app.Application
import com.example.rover.dagger.DaggerRoverComponent
import com.example.rover.dagger.RoverComponent

class RoverApplication : Application() {
    val roverComponent: RoverComponent = DaggerRoverComponent.builder().application(this).build()
}