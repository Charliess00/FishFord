package com.example.fishford

import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


@Suppress("DEPRECATION")
class Registration : AppCompatActivity() {

    private lateinit var edEmail: EditText
    private lateinit var edName: EditText
    private lateinit var edGroup: EditText
    private lateinit var edDopGroupe: EditText
    private lateinit var edType: Spinner
    private lateinit var mDataBase: DatabaseReference
    private var USER_KEY = "User"
    private lateinit var reg: Button
    private lateinit var btngen: Button
    private lateinit var pass: TextView
    private lateinit var btn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

            btn = findViewById(R.id.back)
            reg = findViewById(R.id.btnreg)
            edName = findViewById(R.id.edName)
            edEmail = findViewById(R.id.edEmail)
            edGroup = findViewById(R.id.edGroup)
            edDopGroupe = findViewById(R.id.edDGroup)
            edType = findViewById(R.id.RegType)
            btngen = findViewById(R.id.btngen)
            pass = findViewById(R.id.pasgen)
            mDataBase = FirebaseDatabase.getInstance().getReference(USER_KEY)

        fun buttonundisable() {
            reg.isEnabled = true
            reg.backgroundTintList = resources.getColorStateList(R.color.active)
            reg.setTextColor(resources.getColorStateList(R.color.white))
        }


        fun gen(){
            val chars = ('a' .. 'Z') + ('A'..'Z') + ('0'..'9')
            fun password(): String = List(8) { chars.random() }.joinToString("")
            pass.text = password()
        }

        btngen.setOnClickListener {
            gen()
            buttonundisable()
        }

        fun String.isEmailValid(): Boolean {
            return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this)
                .matches()
        }


        btn.setOnClickListener {
            onBackPressed()
        }

        reg.setOnClickListener {
            val id: String = mDataBase.key.toString()
            val name = edName.text.toString().trim()
            val email = edEmail.text.toString().trim()

            val res = email.isEmailValid()

            val groupe = edGroup.text.toString().trim()
            val dgroupe = edDopGroupe.text.toString().trim()
            val type = edType.selectedItem.toString().trim()
            val password = pass.text.toString().trim()

            if(name.isEmpty()){
                edName.error = "Обязательное поле!"
                //return@setOnClickListener
            } else{
                edName.error = null
            }
            if (!res){
                edEmail.error = "Некорректная почта"
                //return@setOnClickListener
            }else{
                edEmail.error = null
            }
            if (groupe.isEmpty()){
                edGroup.error = "Обязательное поле!"
                //return@setOnClickListener
            }else{
                edGroup.error = null
            }
            if (dgroupe.isEmpty()){
                edDopGroupe.error = "Обязательное поле!"
                //return@setOnClickListener
            }else{
                edDopGroupe.error = null
            }
            if (
                name.isNotEmpty()
                and res
                and groupe.isNotEmpty()
                and dgroupe.isNotEmpty()
            ) {
                val newUser = RegUsers(id, name, email, type, groupe, dgroupe, password)
                mDataBase.push().setValue(newUser)

                Toast.makeText(this, "Успешно добавлено!", Toast.LENGTH_SHORT).show()
            } else{
                return@setOnClickListener
            }
        }

    }
}