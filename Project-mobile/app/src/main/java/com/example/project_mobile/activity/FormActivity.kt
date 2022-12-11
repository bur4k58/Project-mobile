package com.example.project_mobile.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.example.project_mobile.OverviewFragment
import com.example.project_mobile.R
import com.example.project_mobile.adapters.OverviewAdapter
import com.example.project_mobile.data.Attributes
import com.example.project_mobile.data.JsonBase
import com.example.project_mobile.utility.Utility
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.osmdroid.util.GeoPoint
import java.io.IOException
import java.net.URL
import java.net.URLEncoder

class FormActivity : AppCompatActivity() {
    private var searchStreet: EditText? = null
    private var searchDistrict: EditText? = null
    private var searchPostCode: EditText? = null
    private var submitButton: Button? = null
    private var genderToilet: RadioGroup? = null
    private var babyToilet: Switch? = null
    private var wheelToilet: Switch? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        val list = if (intent.extras != null) intent.getSerializableExtra("dataList") as MutableList<Attributes> else Utility().getAttributesList()

        searchStreet = findViewById(R.id.naamStraat)
        searchDistrict = findViewById(R.id.naamDistrict)
        searchPostCode = findViewById(R.id.naamPostCode)
        submitButton = findViewById(R.id.button_submit)
        genderToilet = findViewById(R.id.tip_options)
        babyToilet = findViewById(R.id.luier_switch)
        wheelToilet = findViewById(R.id.round_up_switch)

        submitButton?.setOnClickListener {
            //val url = URL(urlNominatim + "search?q=" + URLEncoder.encode(searchField?.text.toString(), "UTF-8") + "&format=json")
            it.hideKeyboard()
            //getAddressOrLocation(url)
        }

        //Bottom navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navi)
        bottomNavigationView.selectedItemId = R.id.form
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.form -> return@OnNavigationItemSelectedListener true
                R.id.map -> {
                    val mySuperIntent = Intent(applicationContext, MapActivity::class.java)
                    mySuperIntent.action = Intent.ACTION_SEND
                    mySuperIntent.putExtra("dataList", list as java.io.Serializable)
                    startActivity(mySuperIntent)
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.list -> {
                    val mySuperIntent = Intent(applicationContext, MainActivity::class.java)
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
    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
    /*private fun getAddressOrLocation(url : EditText?) {
        var searchReverse = false
        Thread(Runnable {
            searchReverse = (url.toString().indexOf("reverse", 0, true) > -1)
            val client = OkHttpClient()
            val response: Response
            val request = Request.Builder()
                .url(url)
                .build()
            response = client.newCall(request).execute()

            val result = response.body!!.string()

            runOnUiThread {
                val jsonString = StringBuilder(result!!)
                Log.d("be.ap.edu.mapsaver", jsonString.toString())

                val parser: Parser = Parser.default()

                if (searchReverse) {
                    val obj = parser.parse(jsonString) as JsonObject


                }
                else {
                    val array = parser.parse(jsonString) as JsonArray<JsonObject>

                    if (array.size > 0) {
                        val obj = array[0]
                        // mapView center must be updated here and not in doInBackground because of UIThread exception
                        val geoPoint = GeoPoint(obj.string("lat")!!.toDouble(), obj.string("lon")!!.toDouble())
                    }
                    else {
                        Toast.makeText(applicationContext, "Address not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }).start()*/
        /*fun initForm(){
            getAddressOrLocation(searchStreet)
        val database = Firebase.database
        //if
        database.getReference("toilets").get().addOnSuccessListener {
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    val body = response.body?.string()
                    val gson = GsonBuilder().create()
                    val toilet = gson.fromJson(body, JsonBase::class.java)
                    for (i in 0 until toilet.features.size) {
                        database.getReference("toilets")
                            .child(toilet.features.get(i).attributes.ID.toString()).setValue(
                            Attributes(
                                private var searchStreet: EditText? = null
                                    private var searchDistrict: EditText? = null
                                    private var searchPostCode: EditText? = null
                                    private var submitButton: Button? = null
                                    private var genderToilet: RadioGroup? = null
                                    private var babyToilet: Switch? = null
                                    private var wheelToilet: Switch? = null
                                searchStreet,
                                searchStreet,
                                searchStreet,
                                searchStreet,
                                "",
                                searchPostCode,
                                searchDistrict,
                                genderToilet,

                                toilet.features.get(i).attributes.LUIERTAFEL,
                                toilet.features.get(i).attributes.LAT,
                                toilet.features.get(i).attributes.LONG,
                                toilet.features.get(i).attributes.INTEGRAAL_TOEGANKELIJK,
                                toilet.features.get(i).geometry.x,
                                toilet.features.get(i).geometry.y
                            )
                        )
                    }
                }

                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    println("Get json fail")
                }
            })
        }
    }*/
}