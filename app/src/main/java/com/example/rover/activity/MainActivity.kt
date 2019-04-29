package com.example.rover.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.example.rover.R
import com.example.rover.application.RoverApplication
import com.example.rover.databinding.ActivityMainBinding
import com.example.rover.fragment.DetailFragment
import com.example.rover.fragment.MainFragment

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as RoverApplication).roverComponent.inject(this)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        if (supportFragmentManager.findFragmentById(R.id.fragment_host) == null) {
            initializeMainFragment()
        }
    }



    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

fun FragmentActivity.initializeMainFragment() {
    val mainFragment = MainFragment()
    supportFragmentManager.beginTransaction()
        .add(R.id.fragment_host, mainFragment)
        .commit()
}

fun FragmentActivity.navigateToDetailFragment(imgSrc: String?, imgTitle: String) {
    val detailFragment = DetailFragment.newInstance(imgSrc, imgTitle)
    supportFragmentManager.beginTransaction()
        .replace(R.id.fragment_host, detailFragment)
        .addToBackStack(DetailFragment::class.java.name)
        .commit()
}
