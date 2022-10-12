package com.example.fishford

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


@Suppress("DEPRECATION")
class Registration : AppCompatActivity() {


    private lateinit var edEmail: EditText
    private lateinit var edName: EditText
    private lateinit var edGroup: EditText
    private lateinit var edDopGroupe: EditText
    private lateinit var edType: AutoCompleteTextView
    private lateinit var mDataBase: DatabaseReference
    private var USER_KEY = "User"
    private lateinit var reg: Button
    private lateinit var btngen: Button
    private lateinit var pass: TextView
    private lateinit var btn: ImageView
    private lateinit var mAuth: FirebaseAuth


    override fun onResume() {
        super.onResume()

        val arrayList = arrayOf("Student", "Teacher", "Admin")
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, arrayList)

        edType.setAdapter(arrayAdapter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        //inType = findViewById(R.id.InType)

        edType = findViewById(R.id.RegType)
        btn = findViewById(R.id.back)
        reg = findViewById(R.id.btnreg)
        edName = findViewById(R.id.edName)
        edEmail = findViewById(R.id.edEmail)
        edGroup = findViewById(R.id.edGroup)
        edDopGroupe = findViewById(R.id.edDGroup)
        btngen = findViewById(R.id.btngen)
        pass = findViewById(R.id.pasgen)
        mDataBase = FirebaseDatabase.getInstance().getReference(USER_KEY)
        mAuth = Firebase.auth

        fun buttonundisable() {
            reg.isEnabled = true
            reg.backgroundTintList = resources.getColorStateList(R.color.active)
            reg.setTextColor(resources.getColorStateList(R.color.white))
        }


        fun copyText(text: CharSequence) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("copy text", text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Скопировано в буфер обмена", Toast.LENGTH_SHORT).show()
        }

        fun gen() {
            val chars = ('a'..'Z') + ('A'..'Z') + ('0'..'9')
            fun password(): String = List(8) { chars.random() }.joinToString("")
            pass.text = password()
        }

        pass.setOnClickListener {
            copyText(pass.text.toString())
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
            val type = edType.text.toString().trim()
            val password = pass.text.toString().trim()

            if (name.isEmpty()) {
                edName.error = "Обязательное поле!"
                //return@setOnClickListener
            } else {
                edName.error = null
            }
            if (!res) {
                edEmail.error = "Некорректная почта"
                //return@setOnClickListener
            } else {
                edEmail.error = null
            }
            if (groupe.isEmpty()) {
                edGroup.error = "Обязательное поле!"
                //return@setOnClickListener
            } else {
                edGroup.error = null
            }
            if (dgroupe.isEmpty()) {
                edDopGroupe.error = "Обязательное поле!"
                //return@setOnClickListener
            } else {
                edDopGroupe.error = null
            }
            if (
                name.isNotEmpty()
                and res
                and groupe.isNotEmpty()
                and dgroupe.isNotEmpty()
            ) {
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                    OnCompleteListener {
                        if (it.isSuccessful){
                            val newUser = RegUsers(id, name, email, type, groupe, dgroupe)
                            mDataBase.push().setValue(newUser)

                            Toast.makeText(this, "Успешно!", Toast.LENGTH_SHORT).show()
                            tohp()
                        }else {
                            Toast.makeText(this, "Ошибка при регистрации!", Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                return@setOnClickListener
            }
        }

    }

    private fun tohp(){
        val tohome = Intent(this, HomePage::class.java)
        tohome.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(tohome)
    }

    override fun onStart() {
        super.onStart()

        //val cUser = mAuth.currentUser


    }
}