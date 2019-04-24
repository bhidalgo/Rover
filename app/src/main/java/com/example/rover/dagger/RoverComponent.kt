package com.example.rover.dagger

import com.example.rover.application.RoverApplication
import com.example.rover.fragment.MainFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RoverModule::class])
interface RoverComponent {
    fun inject(fragment: MainFragment)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: RoverApplication): Builder
        fun build(): RoverComponent
    }
}