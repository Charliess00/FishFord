package com.example.fishford

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*

class GroupeAd : AppCompatActivity() {
    private lateinit var btn: ImageView
    private lateinit var btnad: Button
    private lateinit var congr: TextInputLayout
    private lateinit var edGroup: AutoCompleteTextView
    private lateinit var mDataBase: DatabaseReference
    private lateinit var listgroup: ArrayList<String>


    override fun onResume() {
        super.onResume()

        listgroup = arrayListOf()
        getDataFromDB()
        val adaptergroup = ArrayAdapter(this, R.layout.dropdown_item, listgroup)

        edGroup.setAdapter(adaptergroup)
    }

    fun getDataFromDB(){
        mDataBase = FirebaseDatabase.getInstance().getReference("Group")

        mDataBase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){

                    for(ds in snapshot.children){
                        val group = ds.child("data").value.toString()
                        Log.d("MyLog",group)
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
        setContentView(R.layout.activity_groupe_ad)

        btn = findViewById(R.id.back_gr)
        btnad = findViewById(R.id.btnad)
        edGroup = findViewById(R.id.edgGroup)
        congr = findViewById(R.id.cont)

        btn.setOnClickListener {
            onBackPressed()
        }

        btnad.setOnClickListener {
            val group = edGroup.text.toString().trim()
            val groupeValid = listgroup.find { it -> group.equals(it) }

            if (group.isNotEmpty()) {
                if (groupeValid == null) {
                    congr.error = null
                    Toast.makeText(this, "Успешно!", Toast.LENGTH_SHORT).show()

                    mDataBase = FirebaseDatabase.getInstance().getReference("Group")
                    mDataBase.push().child("data").setValue(group)
                } else {
                    congr.error = "Группа уже есть в базе!"
                }
            } else {
                congr.error = "Обязательное поле"
            }
        }
    }
}