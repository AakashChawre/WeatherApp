package com.weatherapp.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.weatherapp.MainActivity
import com.weatherapp.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
        startActivity(Intent(this,MainActivity::class.java).addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES))
        finish()
        },3000)
    }
}