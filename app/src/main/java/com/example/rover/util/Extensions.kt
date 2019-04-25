package com.example.rover.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.rover.application.RoverApplication
import com.example.rover.dagger.RoverComponent

val Fragment.roverComponent: RoverComponent
    get() = (activity?.application as RoverApplication).roverComponent

fun Fragment.enableBackButton(enabled: Boolean) {
    (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(enabled)
}