package com.example.fishford

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

    private lateinit var inemail : EditText
    private lateinit var inpass : EditText
    private lateinit var btnlogin : Button
    private lateinit var errmas : TextView
    private lateinit var flag : CheckBox
    private lateinit var nopass : TextView
    private lateinit var mAuth: FirebaseAuth

    private fun tohp(){
        val tohome = Intent(this, HomePage::class.java)
        startActivity(tohome)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if(currentUser != null){
            Toast.makeText(this, "Вы успешно авторизованы!", Toast.LENGTH_SHORT).show()
            tohp()

        }else {
            Toast.makeText(this,"Необходима авторизация!",Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()




        nopass.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Забыли пароль?")
            builder.setMessage("Для сброса пароля обратитесь к своему системному администратору!")
            builder.setNegativeButton("OK"){ _, _ ->}

            val alert11: AlertDialog = builder.create()
            alert11.show()

            val bq: Button = alert11.getButton(DialogInterface.BUTTON_NEGATIVE)
            bq.setTextColor(ContextCompat.getColor(this, R.color.active))
        }

        btnlogin.setOnClickListener{

            val email = inemail.text.toString().trim()
            val pass = inpass.text.toString().trim()
           // val lpass = pass.length

            /*
            fun inemailerr(){
                inemail.setHintTextColor(ContextCompat.getColor(this, R.color.err))
                inemail.backgroundTintList = ContextCompat.getColorStateList(this, R.color.err)
            }

            fun inpasserr(){
                inpass.setHintTextColor(ContextCompat.getColor(this, R.color.err))
                inpass.backgroundTintList = ContextCompat.getColorStateList(this, R.color.err)
            }

            fun inpassact(){
                inpass.setHintTextColor(ContextCompat.getColor(this, R.color.active))
                inpass.backgroundTintList = ContextCompat.getColorStateList(this, R.color.active)
            }

            fun inemailact(){
                inemail.setHintTextColor(ContextCompat.getColor(this, R.color.active))
                inemail.backgroundTintList = ContextCompat.getColorStateList(this, R.color.active)
            }
             */
            if  (email.isEmpty() and pass.isEmpty()){
                //inemailerr()
                //inpasserr()
                errmas.text = getString(R.string.errmas2)
                errmas.isVisible = true
                return@setOnClickListener
                }
            if (email.isEmpty() and pass.isNotEmpty()){
                //inemailerr()
                //inpassact()
                errmas.text = getString(R.string.errmasemail)
                errmas.isVisible = true
                return@setOnClickListener
            }
            if (email.isNotEmpty() and pass.isEmpty()){
                //inemailact()
                //inpasserr()
                errmas.text = getString(R.string.errmaspass)
                errmas.isVisible = true
                return@setOnClickListener
            }

            if  (flag.isChecked) {


                if(email.isNotEmpty() and pass.isNotEmpty()){
                    mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            flag.buttonTintList =
                                ContextCompat.getColorStateList(this, R.color.active)
                            errmas.isVisible = false
                            tohp()
                        } else {
                            //inemailact()
                            //inpassact()
                            errmas.text = getString(R.string.valerr)
                            errmas.isVisible = true
                        }
                    }


                }
                }else{
                //inemailact()
                //inpassact()
                flag.buttonTintList = ContextCompat.getColorStateList(this, R.color.err)
                errmas.text = getString(R.string.errmasbox)
                errmas.isVisible = true
            }

        }
    }

    private fun init() {
        inemail = findViewById(R.id.InEmail)
        inpass = findViewById(R.id.InPass)
        btnlogin = findViewById(R.id.btnlogin)
        errmas = findViewById(R.id.errmas)
        flag = findViewById(R.id.rules)
        nopass = findViewById(R.id.no_pass)
        mAuth = Firebase.auth
    }


    override fun onStart() {
        super.onStart()

        val currentUser = mAuth.currentUser

        updateUI(currentUser)
    }
}