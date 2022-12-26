package com.example.fishford

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.time.LocalDate


@Suppress("DEPRECATION")
class HomePage : AppCompatActivity() {

    private lateinit var exit: ImageView
    private lateinit var option: ImageView
    private lateinit var table_upload_btn: ImageView
    private lateinit var databaseReference: DatabaseReference
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var cuid: String = mAuth.currentUser?.uid.toString()
    private var refPhoto = FirebaseStorage.getInstance().getReference("/avatar/$cuid")
    private lateinit var edname: TextView
    private lateinit var edgroupe: TextView
    private lateinit var eddopgroupe: TextView
    private lateinit var edtype: TextView
    private lateinit var week: TextView
    private lateinit var data: TextView
    private lateinit var selectedImage: ImageView
    private lateinit var uriExel: Uri
    private lateinit var refExel: StorageReference
    var loading = LoadingDialog(this)
    var IMAGE_REQ_CODE = 0
    var TABLE_REQ_CODE = 1
    var name = ""
    var groupe = ""
    var fullname = ""
    var dopgroupe = ""
    var type = ""
    var photoURL = ""

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
        table_upload_btn = findViewById(R.id.upload_btn)
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
        selectedImage = findViewById(R.id.selectedImage)


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

        selectedImage.setOnClickListener {
            Log.d("MyLog", "Click photo")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select image"), IMAGE_REQ_CODE)
        }

        table_upload_btn.setOnClickListener{
            Log.d("Table", "Click table")

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            startActivityForResult(Intent.createChooser(intent, "Select xlxs"), TABLE_REQ_CODE)
        }

        option.setOnClickListener {
            if (type == "Student"){
                val toreset = Intent(this, ResetPas::class.java)
                startActivity(toreset)
            } else if (type == "Admin" || type == "Leader group"){
                toOptionPage()
            }
        }
    }

    private fun uploadExelToFirebaseStorage(ref: StorageReference) {
        ref.putFile(uriExel)
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
                refPhoto.downloadUrl.addOnSuccessListener {
                    photoURL = it.toString()
                    Picasso.get().load(photoURL)
                        .placeholder(R.drawable.no_avatar)
                        .into(selectedImage)

                }

                fullname = it.child("name").value.toString()
                edname.text = fullname
                groupe = it.child("groupe").value.toString()
                refExel = FirebaseStorage.getInstance().getReference("/exel/$groupe")
//                table = FirebaseStorage.getInstance().getReference("/exel/$groupe").toString()
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

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == TABLE_REQ_CODE && data != null) {
                Log.d("Table", "Table selected")

                uriExel = data.data!!
                uploadExelToFirebaseStorage(refExel)
            }
            if (requestCode == IMAGE_REQ_CODE && data != null) {
                Log.d("MyLog", "Photo selected")

                selectedPhotoUri = data.data
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

                selectedImage.setImageBitmap(bitmap)
                uploadImageToFirebaseStorage(refPhoto)
            }
        }
    }

    private fun uploadImageToFirebaseStorage(ref: StorageReference) {
        if (selectedPhotoUri == null) return



        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("MyLog", "Upload image to Storage: ${it.metadata?.path}")

            ref.downloadUrl.addOnSuccessListener {
                setUserUrlImage(it.toString())
            }
            }
    }

    private fun setUserUrlImage(avatarUrl: String) {
        Log.d("MyLog", avatarUrl)
        val ref = databaseReference.child(cuid)
        ref.child("avatarUrl").setValue(avatarUrl)
    }

    private fun disLoading() {
        Log.d("MyLog", "Загрузка завершена.")
        loading.isDis()
    }

}