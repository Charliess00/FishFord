package com.example.fishford

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class Options : AppCompatActivity() {
    private lateinit var btn_reg: ConstraintLayout
    private lateinit var btn_groupe: ConstraintLayout
    private lateinit var btn_reset: ConstraintLayout
    private lateinit var btn: ImageView
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var cuid: String = mAuth.currentUser?.uid.toString()
    private lateinit var databaseReference: DatabaseReference
    var type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)
        init()
        sattings_page()

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

        btn_reset.setOnClickListener{
            val tores = Intent(this, ResetPas::class.java)
            startActivity(tores)
        }
    }

    private fun sattings_page() {
        databaseReference = FirebaseDatabase.getInstance().getReference("User")
        databaseReference.child(cuid).get().addOnSuccessListener {
            if (it.exists()) {
                type = it.child("type").value.toString()

                if (type == "Leader group"){
                    btn_groupe.isVisible = false
                }
            }
        }
    }

    fun init(){
        btn_reg = findViewById(R.id.btn_reg)
        btn_groupe = findViewById(R.id.btn_group)
        btn = findViewById(R.id.back_tohp)
        btn_reset = findViewById(R.id.btn_reset)
    }
}