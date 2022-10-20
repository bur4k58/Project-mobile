package com.example.project_mobile.b.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.project_mobile.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        //Bottom navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navi)
        bottomNavigationView.selectedItemId = R.id.list
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.list -> return@OnNavigationItemSelectedListener true
                R.id.map -> {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }
}