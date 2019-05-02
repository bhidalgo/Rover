package com.example.rover.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.example.rover.R
import com.example.rover.application.RoverApplication
import com.example.rover.databinding.ActivityMainBinding
import com.example.rover.detail.DetailFragment
import com.example.rover.main.fragment.MainFragment

class MainActivity : AppCompatActivity() {
    var filterEnabled = false
    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as RoverApplication).roverComponent.inject(this)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (supportFragmentManager.findFragmentById(R.id.fragment_host) == null) {
            initializeMainFragment()
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Trigger the current fragment's method as well.
        supportFragmentManager.findFragmentById(R.id.fragment_host)?.onOptionsItemSelected(item)

        return when (item.itemId) {
            //Back button pressed.
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (filterEnabled) {
            menuInflater.inflate(R.menu.filter_menu, menu)
        }

        return super.onCreateOptionsMenu(menu)
    }
}
