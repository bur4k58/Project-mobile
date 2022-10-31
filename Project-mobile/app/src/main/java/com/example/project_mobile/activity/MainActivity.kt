package com.example.project_mobile.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.project_mobile.R
import com.example.project_mobile.data.JsonBase
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getToiletsJson()

//        https://geodata.antwerpen.be/arcgissql/rest/services/P_Portal/portal_publiek1/MapServer/8/query?where=1%3D1&outFields=ID,OMSCHRIJVING,STRAAT,HUISNUMMER,POSTCODE,DISTRICT,DOELGROEP,LUIERTAFEL,LAT,LONG,INTEGRAAL_TOEGANKELIJK&outSR=4326&f=json
        //Bottom navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navi)
        bottomNavigationView.selectedItemId = R.id.list
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.list -> return@OnNavigationItemSelectedListener true
                R.id.map -> {
                    startActivity(Intent(applicationContext, MapActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }
    fun getToiletsJson() {
        println("attempting to get JSON")
        val url = "https://geodata.antwerpen.be/arcgissql/rest/services/P_Portal/portal_publiek1/MapServer/8/query?where=1%3D1&outFields=ID,OMSCHRIJVING,STRAAT,HUISNUMMER,POSTCODE,DISTRICT,DOELGROEP,LUIERTAFEL,LAT,LONG,INTEGRAAL_TOEGANKELIJK&outSR=4326&f=json"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                val body= response?.body?.string()
                println(body)   
                val gson = GsonBuilder().create()
                val toilet = gson.fromJson(body, JsonBase::class.java)
                for(i in 0 until toilet.features.size){
                    println("\nItem nr: ${i}\nXY: ${toilet.features.get(i).geometry}\nDetails: \n${toilet.features.get(i).attributes}\n--------------------\n")
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                println("Get json fail")
            }
        })
    }
}