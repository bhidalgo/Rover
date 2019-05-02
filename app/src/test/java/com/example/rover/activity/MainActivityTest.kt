package com.example.rover.activity

import androidx.fragment.app.Fragment
import androidx.test.core.app.ActivityScenario
import com.example.rover.BaseTest
import com.example.rover.R
import com.example.rover.detail.DetailFragment
import com.example.rover.main.fragment.MainFragment
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.robolectric.fakes.RoboMenu
import org.robolectric.fakes.RoboMenuItem

class MainActivityTest : BaseTest() {
    private lateinit var mTestScenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        mTestScenario = getActivityScenario()
        /* Activity is in state RESUMED */
    }

    @Test
    fun `when first launching the activity then we should initialize the main fragment`() {
        mTestScenario
            .onActivity {
                val currentFragment = it.supportFragmentManager.findFragmentById(R.id.fragment_host)!!
                assertTrue(currentFragment is MainFragment)
            }
    }

    @Test
    fun `when activity is recreated then we should not initialize another main fragment`() {
        var firstFragment: Fragment? = null

        mTestScenario
            .onActivity {
                firstFragment = it.supportFragmentManager.findFragmentById(R.id.fragment_host)!!
            }
            .recreate()
            .onActivity {
                val currentFragment = it.supportFragmentManager.findFragmentById(R.id.fragment_host)
                assertEquals(firstFragment!!, currentFragment)
            }
    }

    @Test
    fun `when back button is pressed then we should call on back pressed`() {
        mTestScenario
            .onActivity {
                val activitySpy = spy(it)
                val testBackButtonMenuItem = RoboMenuItem(android.R.id.home)
                activitySpy.onOptionsItemSelected(testBackButtonMenuItem)
                verify(activitySpy).onBackPressed()
            }
    }

    @Test
    fun `when filter is enabled then we should display the filter menu`() {
        mTestScenario
            .onActivity {
                val testMenu = RoboMenu()
                it.filterEnabled = true
                it.onCreateOptionsMenu(testMenu)
                assertNotNull(testMenu.findItem(R.id.filter))
            }
    }

    @Test
    fun `when initializing the main fragment then we should add the main fragment to the layout`() {
        mTestScenario
            .onActivity {
                val currentFragment = it.supportFragmentManager.findFragmentById(R.id.fragment_host)!!

                it.initializeMainFragment()

                val mainFragment = (it.supportFragmentManager.findFragmentById(R.id.fragment_host) as? MainFragment)!!

                assertNotEquals(currentFragment, mainFragment)
            }
    }

    @Test
    fun `when navigating to the detail fragment then we should replace the current fragment with the detail fragment`() {
        mTestScenario
            .onActivity {
                val currentFragment = it.supportFragmentManager.findFragmentById(R.id.fragment_host)!!

                it.navigateToDetailFragment("", "")

                val detailFragment =
                    (it.supportFragmentManager.findFragmentById(R.id.fragment_host) as? DetailFragment)!!

                assertNotEquals(currentFragment, detailFragment)
            }
    }

    private fun getActivityScenario(): ActivityScenario<MainActivity> {
        return ActivityScenario.launch(MainActivity::class.java)
    }
}