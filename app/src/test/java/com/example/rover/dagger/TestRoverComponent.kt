package com.example.rover.dagger

import com.example.rover.application.RoverApplication
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [TestRoverModule::class])
interface TestRoverComponent : RoverComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: RoverApplication): Builder
        fun build(): TestRoverComponent
    }
}