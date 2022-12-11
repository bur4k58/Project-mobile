package com.example.project_mobile.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.example.project_mobile.OverviewFragment
import com.example.project_mobile.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.net.URL
import java.net.URLEncoder

class FormActivity : AppCompatActivity() {
    private var searchStreet: EditText? = null
    private var searchNumber: EditText? = null
    private var searchDistrict: EditText? = null
    private var searchPostCode: EditText? = null
    private var submitButton: Button? = null
    private var genderToilet: RadioGroup? = null
    private var babyToilet: Switch? = null
    private var wheelToilet: Switch? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        supportFragmentManager.beginTransaction().replace(R.id.form_container, OverviewFragment()).commit()

        searchStreet = findViewById(R.id.naamStraat)
        searchNumber = findViewById(R.id.nummerStraat)
        searchDistrict = findViewById(R.id.naamDistrict)
        searchPostCode = findViewById(R.id.naamPostCode)
        submitButton = findViewById(R.id.button_submit)
        genderToilet = findViewById(R.id.tip_options)
        babyToilet = findViewById(R.id.luier_switch)
        wheelToilet = findViewById(R.id.round_up_switch)

        submitButton?.setOnClickListener {
            val url = URL(urlNominatim + "search?q=" + URLEncoder.encode(searchField?.text.toString(), "UTF-8") + "&format=json")
            it.hideKeyboard()
            getAddressOrLocation(url)
        }

        //Bottom navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navi)
        bottomNavigationView.selectedItemId = R.id.form
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.form -> return@OnNavigationItemSelectedListener true
                R.id.list -> {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })

    }
    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
    private fun initForm(){

    }
}