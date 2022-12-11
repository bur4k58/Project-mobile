package com.example.project_mobile.utility

import com.example.project_mobile.adapters.OverviewAdapter
import com.example.project_mobile.data.Attributes
import com.example.project_mobile.data.JsonBase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

public class Utility {
    private lateinit var attributesList: MutableList<Attributes>

    fun getAttributesList(): MutableList<Attributes> {
        println("attempting to get JSON")
        val url = "https://geodata.antwerpen.be/arcgissql/rest/services/P_Portal/portal_publiek1/MapServer/8/query?where=1%3D1&outFields=ID,OMSCHRIJVING,STRAAT,HUISNUMMER,POSTCODE,DISTRICT,DOELGROEP,LUIERTAFEL,LAT,LONG,INTEGRAAL_TOEGANKELIJK&outSR=4326&f=json"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        val database = Firebase.database
        attributesList = mutableListOf()
        //if
        database.getReference("toilets").get().addOnSuccessListener {
            //Log.i("firebase", "Got value ${it.value}")
            if(it.value != null)
            {
                val itr = it.children?.iterator()
                var test=0
                println("----------------------------------------------")
                for (jobSnapshot in it.getChildren())
                {
                    val toilet: Attributes? = jobSnapshot.getValue(Attributes::class.java)
                    if (toilet != null) {
                        attributesList.add(test, Attributes(toilet.ID,toilet.OMSCHRIJVING,toilet.STRAAT,toilet.HUISNUMMER,toilet.POSTCODE,toilet.DISTRICT,toilet.DOELGROEP,toilet.LUIERTAFEL,toilet.LAT,toilet.LONG,toilet.INTEGRAAL_TOEGANKELIJK,toilet.x,toilet.y))
                    }
                    test++
                }
            }
            else{
                client.newCall(request).enqueue(object: Callback {
                    override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                        val body= response.body?.string()
                        val gson = GsonBuilder().create()
                        val toilet = gson.fromJson(body, JsonBase::class.java)
                        for(i in 0 until toilet.features.size){
                            database.getReference("toilets").child(toilet.features.get(i).attributes.ID.toString()).setValue(
                                Attributes(toilet.features.get(i).attributes.ID,toilet.features.get(i).attributes.OMSCHRIJVING,toilet.features.get(i).attributes.STRAAT,toilet.features.get(i).attributes.HUISNUMMER,toilet.features.get(i).attributes.POSTCODE,toilet.features.get(i).attributes.DISTRICT,toilet.features.get(i).attributes.DOELGROEP,toilet.features.get(i).attributes.LUIERTAFEL,toilet.features.get(i).attributes.LAT,toilet.features.get(i).attributes.LONG,toilet.features.get(i).attributes.INTEGRAAL_TOEGANKELIJK,toilet.features.get(i).geometry.x,toilet.features.get(i).geometry.y)
                            )
                            attributesList.add(i, Attributes(toilet.features.get(i).attributes.ID,toilet.features.get(i).attributes.OMSCHRIJVING,toilet.features.get(i).attributes.STRAAT,toilet.features.get(i).attributes.HUISNUMMER,toilet.features.get(i).attributes.POSTCODE,toilet.features.get(i).attributes.DISTRICT,toilet.features.get(i).attributes.DOELGROEP,toilet.features.get(i).attributes.LUIERTAFEL,toilet.features.get(i).attributes.LAT,toilet.features.get(i).attributes.LONG,toilet.features.get(i).attributes.INTEGRAAL_TOEGANKELIJK,toilet.features.get(i).geometry.x,toilet.features.get(i).geometry.y))
                        }
                    }
                    override fun onFailure(call: okhttp3.Call, e: IOException) {
                        println("Get json fail")
                    }
                })
            }
        }
        return attributesList
    }

    fun success() {
        return
    }
}

