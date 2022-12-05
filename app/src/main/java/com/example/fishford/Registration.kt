package com.example.fishford

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.fishford.items.LoadingDialog
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


@Suppress("DEPRECATION")
class Registration : AppCompatActivity() {

    private lateinit var edEmail: EditText
    private lateinit var edName: EditText
    private lateinit var edGroup: AutoCompleteTextView
    private lateinit var edDopGroupe: EditText
    private lateinit var edType: AutoCompleteTextView
    private lateinit var mDataBase: DatabaseReference
    private lateinit var cuid: String
    private lateinit var mDataBase2: DatabaseReference
    private lateinit var reg: Button
    private lateinit var btngen: Button
    private lateinit var pass: TextView
    private lateinit var btn: ImageView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mAuth2: FirebaseAuth
    private lateinit var listgroup: ArrayList<String>
    private lateinit var databaseReference: DatabaseReference
    private var loading = LoadingDialog(this)
    private var ugroupe = ""
    private var udopgroupe = ""
    private var utype = ""


    override fun onResume() {
        super.onResume()

        val arrayList = arrayOf("Student", "Leader group", "Admin")
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, arrayList)

        listgroup = arrayListOf()
        getDataFromDB()
        val adaptergroup = ArrayAdapter(this, R.layout.dropdown_item, listgroup)

        edType.setAdapter(arrayAdapter)
        edGroup.setAdapter(adaptergroup)
        mAuth = Firebase.auth
        cuid = mAuth.currentUser?.uid.toString()
        if (cuid.isNotEmpty()) {
            getUserData()
        }
    }

    private fun getDataFromDB(){
        mDataBase2 = FirebaseDatabase.getInstance().getReference("Group")

        mDataBase2.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){

                    for(ds in snapshot.children){
                        val group = ds.child("data").value.toString()
                        listgroup.add(group)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        edType = findViewById(R.id.RegType)
        btn = findViewById(R.id.back)
        reg = findViewById(R.id.btnreg)
        edName = findViewById(R.id.edName)
        edEmail = findViewById(R.id.edEmail)
        edGroup = findViewById(R.id.edGroup)
        edDopGroupe = findViewById(R.id.edDGroup)
        btngen = findViewById(R.id.btngen)
        pass = findViewById(R.id.pasgen)
        mDataBase = FirebaseDatabase.getInstance().getReference("User")


        val firebaseOptions = FirebaseOptions.Builder()
            .setDatabaseUrl("[https://fishford-d02f6-default-rtdb.firebaseio.com/]")
            .setApiKey("AIzaSyCcfYR4bEg-f8wXRRvAmXYr0EAJz4I_vks")
            .setApplicationId("fishford-d02f6").build()

        mAuth2 = try {
            val myApp = FirebaseApp.initializeApp(applicationContext, firebaseOptions, "AnyAppName")
            FirebaseAuth.getInstance(myApp)
        } catch (e: IllegalStateException) {
            FirebaseAuth.getInstance(FirebaseApp.getInstance("AnyAppName"))
        }
        fun buttonundisable() {
            reg.isEnabled = true
            //reg.backgroundTintList = resources.getColorStateList(R.color.active)
            reg.backgroundTintList = ContextCompat.getColorStateList(this, R.color.active)
            //reg.setTextColor(resources.getColorStateList(R.color.white))
            reg.setTextColor(ContextCompat.getColorStateList(this, R.color.white))

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

            val groupeValid = listgroup.find { groupe == it }
            if (groupeValid != null) {
                Log.d("MyLog", groupeValid)
            } else{
                Log.d("MyLog", "null")}


            if (name.isEmpty()) {
                edName.error = "Обязательное поле!"
            } else {
                edName.error = null
            }
            if (!res) {
                edEmail.error = "Некорректная почта"
            } else {
                edEmail.error = null
            }
            if (groupeValid == null) {
                edGroup.error = "Данной группы нет в базе!"
            } else {
                edGroup.error = null
            }
            if (dgroupe.isEmpty()) {
                edDopGroupe.error = "Обязательное поле!"
            } else {
                edDopGroupe.error = null
            }
            if (
                name.isNotEmpty()
                and res
                and (groupeValid != null)
                and dgroupe.isNotEmpty()
            ) {
                showLoading()

                mAuth2.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val uid = mAuth2.uid.toString()
                        val newUser = RegUsers(id, name, email, type, groupe, dgroupe, password)
                        mDataBase.child(uid).setValue(newUser)

                        mAuth2.signOut()
                        disLoading()
                        Log.d("MyLog", "Reg - Suc")
                        Toast.makeText(this, "Успешно!", Toast.LENGTH_SHORT).show()
                        tohp()
                    } else {
                        disLoading()
                        Log.d("MyLog", "Reg - Suc")
                        Toast.makeText(this, "Ошибка при регистрации!", Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                return@setOnClickListener
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun getUserData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("User")
        databaseReference.child(cuid).get().addOnSuccessListener {
            if (it.exists()) {
                ugroupe = it.child("groupe").value.toString()
                udopgroupe = it.child("dgroupe").value.toString()
                utype = it.child("type").value.toString()
                if (utype == "Leader group"){
                    Log.d("MyLog", "Заблокировано!!")

                    edType.isEnabled = false
                    edType.setText("Student")

                    edGroup.isEnabled = false
                    edGroup.setText(ugroupe)

                    edDopGroupe.isEnabled = false
                    edDopGroupe.setText(udopgroupe)
                }
            }

        }.addOnFailureListener {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading() {
        Log.d("MyLog", "Загрузка...")
        //val loading = LoadingDialog(this)
        loading.showLoading()

    }

    private fun disLoading() {
        Log.d("MyLog", "Загрузка завершена.")
        //val loading = LoadingDialog(this)
        loading.isDis()
    }

    private fun tohp(){
        this.onBackPressed()
    }
}