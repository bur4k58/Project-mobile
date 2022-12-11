package com.example.project_mobile.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.project_mobile.OverviewFragment
import com.example.project_mobile.R
import com.example.project_mobile.data.Attributes
import com.example.project_mobile.data.JsonBase
import com.example.project_mobile.utility.Utility
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = if (intent.extras != null) intent.getSerializableExtra("dataList") as MutableList<Attributes> else Utility().getAttributesList()

        val overviewFragment = OverviewFragment(list)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, overviewFragment).commit()

        //Bottom navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navi)
        bottomNavigationView.selectedItemId = R.id.list
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.list -> return@OnNavigationItemSelectedListener true
                R.id.map -> {
                    val mySuperIntent = Intent(applicationContext, MapActivity::class.java)
                    mySuperIntent.action = Intent.ACTION_SEND
                    mySuperIntent.putExtra("dataList", list as java.io.Serializable)
                    startActivity(mySuperIntent)
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.form -> {
                    val mySuperIntent = Intent(applicationContext, FormActivity::class.java)
                    mySuperIntent.action = Intent.ACTION_SEND
                    mySuperIntent.putExtra("dataList", list as java.io.Serializable)
                    startActivity(mySuperIntent)
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }
}