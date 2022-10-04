package com.example.fishford

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);

        val intent = Intent(this, LoginActivity::class.java)
        // start your next activity
        startActivity(intent)
        finish()
    }
}