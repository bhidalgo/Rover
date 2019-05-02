package com.example.rover.application

import com.example.rover.dagger.DaggerTestRoverComponent

class TestRoverApplication : RoverApplication() {

    override fun onCreate() {
        super.onCreate()
        roverComponent = DaggerTestRoverComponent.builder().application(this).build()
    }
}