package com.example.fishford

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class HomePage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var exit: ImageView
    private lateinit var option: ImageView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var uid: String
    private lateinit var edname: TextView
    private lateinit var edgroupe: TextView
    private lateinit var eddopgroupe: TextView
    private lateinit var edtype: TextView


    private fun toLoginPage() {
        Firebase.auth.signOut()
        val tologin = Intent(this, LoginActivity::class.java)
        startActivity(tologin)
    }

    private fun toOptionPage() {
        val tooption = Intent(this, Registration::class.java)
        startActivity(tooption)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        edname = findViewById(R.id.Name)
        edgroupe = findViewById(R.id.group)
        eddopgroupe = findViewById(R.id.dopgroup)
        edtype = findViewById(R.id.type)

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()

        databaseReference = FirebaseDatabase.getInstance().getReference("User")
        if (uid.isNotEmpty()){
            getUserData()
        }

        exit = findViewById(R.id.exiticon)
        option = findViewById(R.id.sattingsicon)

        exit.setOnClickListener {
            toLoginPage()
        }

        option.setOnClickListener {
           toOptionPage()
        }
   }
    private fun getUserData() {
        databaseReference.child(uid).get().addOnSuccessListener {

            val name = it.child("name").value
            edname.text = name.toString()
            val group = it.child("groupe").value
            edgroupe.text = group.toString()

            if(it.exists()){
                val fullname = it.child("name").value
                edname.text = fullname.toString()
                val groupe = it.child("groupe").value
                edgroupe.text = groupe.toString()
                val dopgroupe = it.child("dgroupe").value
                eddopgroupe.text = dopgroupe.toString()
                val type = it.child("type").value
                edtype.text = type.toString()
                if(type == "Admin" || type == "Teacher"){
                    edgroupe.isVisible = false
                    eddopgroupe.isVisible = false
                }
            } else{
                Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener{
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }

}

}