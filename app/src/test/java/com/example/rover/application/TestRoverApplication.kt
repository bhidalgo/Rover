package com.example.rover.application

import com.example.rover.dagger.DaggerTestRoverComponent
import com.squareup.picasso.Picasso
import java.lang.IllegalStateException

class TestRoverApplication : RoverApplication() {

    override fun onCreate() {
        super.onCreate()

        checkPicassoInstance()

        roverComponent = DaggerTestRoverComponent.builder().application(this).build()
    }

    /**
     * Sets the Picasso singleton instance to avoid IllegalStateExceptions.
     */
    private fun checkPicassoInstance() {
        try {
            Picasso.get()
        } catch (e: IllegalStateException) {
            Picasso.setSingletonInstance(Picasso.Builder(this).build())
        }
    }
}