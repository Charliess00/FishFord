package com.example.fishford

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.fishford.items.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.time.LocalDate


class HomePage : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var exit: ImageView
    private lateinit var option: ImageView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var cuid: String
    private lateinit var edname: TextView
    private lateinit var edgroupe: TextView
    private lateinit var eddopgroupe: TextView
    private lateinit var edtype: TextView
    private lateinit var week: TextView
    private lateinit var data: TextView
    var loading = LoadingDialog(this)
    var name = ""
    var groupe = ""
    var fullname = ""
    var dopgroupe = ""
    var type = ""


    private fun toLoginPage() {
        Firebase.auth.signOut()
        val tologin = Intent(this, LoginActivity::class.java)
        startActivity(tologin)
    }

    private fun toOptionPage() {
        val tooption = Intent(this, Options::class.java)
        startActivity(tooption)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        data = findViewById(R.id.tv_data)
        val datastr = LocalDate.now().toString()
        val datalist = datastr.toList()
        val year = datalist.subList(0, 4)
        val mouth = datalist.subList(5, 7)
        val day = datalist.subList(8, 10)
        val yearstr = year.joinToString(separator = "")
        val mouthstr = mouth.joinToString(separator = "")
        val daystr = day.joinToString(separator = "")
        Log.d("MyLog", "Год: " + yearstr)
        Log.d("MyLog", "Месяц: " + mouthstr)
        Log.d("MyLog", "День: " + daystr)
        data.text = ("$daystr.$mouthstr.$yearstr г.")

        week = findViewById(R.id.tv_week)
        val weekstr = LocalDate.now().dayOfWeek.toString()
        val weeknumb = listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")
        val index = weeknumb.indexOf(weekstr)
        val weeknumbrus = listOf("ПОНЕДЕЛЬНИК", "ВТОРНИК", "СРЕДА", "ЧЕТВЕРГ", "ПЯТНИЦА", "СУББОТА", "ВОСКРЕСЕНЬЕ")
        week.text = weeknumbrus.get(index)

        edname = findViewById(R.id.Name)
        edgroupe = findViewById(R.id.group)
        eddopgroupe = findViewById(R.id.dopgroup)
        edtype = findViewById(R.id.type)

        mAuth = FirebaseAuth.getInstance()
        cuid = mAuth.currentUser?.uid.toString()
        showDialog()
        Log.d("MyLog", "UID пользователя: $cuid")
        if (cuid.isNotEmpty()) {
            getUserData()
        }

        exit = findViewById(R.id.exiticon)
        option = findViewById(R.id.sattingsicon)

        exit.setOnClickListener {
            toLoginPage()
        }

        option.setOnClickListener {
            if (type == "Admin"){
                toOptionPage()
            }else if (type == "Leader group"){
                val toreg = Intent(this, Registration::class.java)
                startActivity(toreg)
            }
//            else if (type == "Student"){
//                val tores = Intent(this, ResetPas::class.java)
//                startActivity(tores)
//            }
        }
    }

    private fun showDialog() {
        Log.d("MyLog", "Загрузка...")
        loading.showLoading()
    }

    private fun getUserData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("User")
        databaseReference.child(cuid).get().addOnSuccessListener {

            Log.d("MyLog", "Чтение данных пользователя: $cuid")

            if (it.exists()) {
                fullname = it.child("name").value.toString()
                edname.text = fullname
                groupe = it.child("groupe").value.toString()
                edgroupe.text = groupe
                dopgroupe = it.child("dgroupe").value.toString()
                eddopgroupe.text = dopgroupe
                type = it.child("type").value.toString()
                edtype.text = type

                if (type == "Admin" || type == "Teacher") {
                    edgroupe.isVisible = false
                    eddopgroupe.isVisible = false
                }

                disLoading()
                Toast.makeText(this, "Синхранизация прошла успешно", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Данные пользователя не найдены", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }

    }

    private fun disLoading() {
        Log.d("MyLog", "Загрузка завершена.")
        loading.isDis()
    }

}