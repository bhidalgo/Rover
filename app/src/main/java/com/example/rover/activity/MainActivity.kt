package com.example.rover.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.rover.R
import com.example.rover.fragment.MainFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(supportFragmentManager.findFragmentById(R.id.fragment_host) == null) {
            supportFragmentManager.beginTransaction().add(R.id.fragment_host, MainFragment()).commit()
        }
    }
}
