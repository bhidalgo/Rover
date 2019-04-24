package com.example.rover.util

import androidx.fragment.app.Fragment
import com.example.rover.application.RoverApplication
import com.example.rover.dagger.RoverComponent

val Fragment.roverComponent: RoverComponent
    get() = (activity?.application as RoverApplication).roverComponent