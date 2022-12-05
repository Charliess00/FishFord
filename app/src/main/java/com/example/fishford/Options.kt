package com.example.fishford

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout

class Options : AppCompatActivity() {
    private lateinit var btn_reg: ConstraintLayout
    private lateinit var btn_groupe: ConstraintLayout
    private lateinit var btn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)
        init()

        btn_reg.setOnClickListener{
            val toreg = Intent(this, Registration::class.java)
            startActivity(toreg)
        }

        btn_groupe.setOnClickListener{
            val togr = Intent(this, GroupeAd::class.java)
            startActivity(togr)
        }

        btn.setOnClickListener{
            onBackPressed()
        }
    }

    fun init(){
        btn_reg = findViewById(R.id.btn_reg)
        btn_groupe = findViewById(R.id.btn_group)
        btn = findViewById(R.id.back_tohp)
    }
}