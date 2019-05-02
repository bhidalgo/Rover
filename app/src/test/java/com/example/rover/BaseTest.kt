package com.example.rover

import com.example.rover.application.TestRoverApplication
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestRoverApplication::class)
abstract class BaseTest {
    inline fun <reified T: Any> Any.cast(): T {
        return this as T
    }
}