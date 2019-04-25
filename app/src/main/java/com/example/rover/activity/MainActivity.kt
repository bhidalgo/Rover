package com.example.rover.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.rover.R
import com.example.rover.databinding.ListItemRoverCardBinding
import com.example.rover.fragment.DetailFragment
import com.example.rover.fragment.MainFragment

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (supportFragmentManager.findFragmentById(R.id.fragment_host) == null) {
            initializeMainFragment()
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            val binding = DataBindingUtil.getBinding<ViewDataBinding>(v)
            when (binding) {
                is ListItemRoverCardBinding -> navigateToDetailFragment(
                    binding.roverPhoto?.imgSrc,
                    binding.roverTextView.text.toString()
                )
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initializeMainFragment() {
        val mainFragment = MainFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_host, mainFragment)
            .commit()
    }

    private fun navigateToDetailFragment(imgSrc: String?, imgTitle: String) {
        val detailFragment = DetailFragment.newInstance(imgSrc, imgTitle)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_host, detailFragment)
            .addToBackStack(DetailFragment::class.java.name)
            .commit()
    }
}
