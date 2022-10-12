package com.example.fishford

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomePage : AppCompatActivity() {
    private lateinit var exit : ImageView
    private lateinit var option : ImageView

    fun toLoginPage(){
        Firebase.auth.signOut()
        val tologin = Intent(this, LoginActivity::class.java)
        startActivity(tologin)
    }

    private fun toOptionPage(){
        val tooption = Intent(this, Registration::class.java)
        startActivity(tooption)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        exit = findViewById(R.id.exiticon)
        option = findViewById(R.id.sattingsicon)

        exit.setOnClickListener{
            toLoginPage()
        }

        option.setOnClickListener{
            toOptionPage()
        }
    }

}