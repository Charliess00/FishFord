package com.example.fishford

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

    private lateinit var inemail: EditText
    private lateinit var inpass: EditText
    private lateinit var btnlogin: Button
    private lateinit var errmas: TextView
    private lateinit var nopass: TextView
    private lateinit var mAuth: FirebaseAuth

    private fun tohp() {
        val tohome = Intent(this, HomePage::class.java)
        startActivity(tohome)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            tohp()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()




        nopass.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Забыли пароль?")
            builder.setMessage("Для сброса пароля обратитесь к своему системному администратору!")
            builder.setNegativeButton("OK") { _, _ -> }

            val alert11: AlertDialog = builder.create()
            alert11.show()

            val bq: Button = alert11.getButton(DialogInterface.BUTTON_NEGATIVE)
            bq.setTextColor(ContextCompat.getColor(this, R.color.active))
        }

        btnlogin.setOnClickListener {

            val email = inemail.text.toString().trim()
            val pass = inpass.text.toString().trim()

            if (
                email.isNotEmpty()
                and pass.isNotEmpty()
            ) {

                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        errmas.isVisible = false
                        tohp()
                    } else {
                        errmas.isVisible = true
                    }
                }
            } else {
                errmas.isVisible = true
                return@setOnClickListener
            }
        }
    }

    private fun init() {
        inemail = findViewById(R.id.InEmail)
        inpass = findViewById(R.id.InPass)
        btnlogin = findViewById(R.id.btnlogin)
        nopass = findViewById(R.id.no_pass)
        errmas = findViewById(R.id.errmas)
        mAuth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()

        val currentUser = mAuth.currentUser

        updateUI(currentUser)
    }
}