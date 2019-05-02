package com.example.rover.activity

import androidx.fragment.app.FragmentActivity
import com.example.rover.R
import com.example.rover.detail.DetailFragment
import com.example.rover.main.fragment.MainFragment

/**
 * Adds MainFragment to the activity's layout.
 */
fun FragmentActivity.initializeMainFragment() {
    val mainFragment = MainFragment()

    supportFragmentManager.beginTransaction()
        .add(R.id.fragment_host, mainFragment, MainFragment::class.java.name)
        .commit()
}

/**
 * Replaces the current fragment with a new DetailFragment
 * @param imgSrc - The URL for the image being loaded.
 * @param imgTitle - The title for the image.
 */
fun FragmentActivity.navigateToDetailFragment(imgSrc: String?, imgTitle: String) {
    val detailFragment = DetailFragment.newInstance(imgSrc, imgTitle)

    supportFragmentManager.beginTransaction()
        .replace(R.id.fragment_host, detailFragment)
        .addToBackStack(DetailFragment::class.java.name)
        .commit()
}