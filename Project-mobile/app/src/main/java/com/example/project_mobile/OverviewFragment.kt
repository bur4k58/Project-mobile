package com.example.project_mobile

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_mobile.adapters.OverviewAdapter
import com.example.project_mobile.data.Attributes
import com.example.project_mobile.data.JsonBase
import com.example.project_mobile.utility.Utility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


class OverviewFragment(private val mainList : MutableList<Attributes>) : Fragment(){
    private lateinit var attributesList: MutableList<Attributes>
    private lateinit var adapter: OverviewAdapter
    private lateinit var recyclerView: RecyclerView
    private var locationPermissionGranted: Boolean = false
    private final var PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: Int = 1
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastKnownLocation: Location
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var filterMen: Switch
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var filterWoman: Switch
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var filterWheelChair: Switch
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var filterChangingTable: Switch
    private lateinit var list:List<Attributes>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_overview, container, false)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireContext())
        attributesList = mainList
        //initAdapter(view)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        lastKnownLocation = Location("")
        getLocationPermission()

        setAdapter(mainList)

        recyclerView.adapter = adapter

        intialiseViews(view)
        addEventHandlers()

        return view
    }

    /*fun getAttributesList(): MutableList<Attributes> {
        println("attempting to get JSON")
        val url = "https://geodata.antwerpen.be/arcgissql/rest/services/P_Portal/portal_publiek1/MapServer/8/query?where=1%3D1&outFields=ID,OMSCHRIJVING,STRAAT,HUISNUMMER,POSTCODE,DISTRICT,DOELGROEP,LUIERTAFEL,LAT,LONG,INTEGRAAL_TOEGANKELIJK&outSR=4326&f=json"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        val database = Firebase.database
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
                            database.getReference("toilets").child(toilet.features.get(i).attributes.ID.toString()).setValue(Attributes(toilet.features.get(i).attributes.ID,toilet.features.get(i).attributes.OMSCHRIJVING,toilet.features.get(i).attributes.STRAAT,toilet.features.get(i).attributes.HUISNUMMER,toilet.features.get(i).attributes.POSTCODE,toilet.features.get(i).attributes.DISTRICT,toilet.features.get(i).attributes.DOELGROEP,toilet.features.get(i).attributes.LUIERTAFEL,toilet.features.get(i).attributes.LAT,toilet.features.get(i).attributes.LONG,toilet.features.get(i).attributes.INTEGRAAL_TOEGANKELIJK,toilet.features.get(i).geometry.x,toilet.features.get(i).geometry.y))
                            attributesList.add(i, Attributes(toilet.features.get(i).attributes.ID,toilet.features.get(i).attributes.OMSCHRIJVING,toilet.features.get(i).attributes.STRAAT,toilet.features.get(i).attributes.HUISNUMMER,toilet.features.get(i).attributes.POSTCODE,toilet.features.get(i).attributes.DISTRICT,toilet.features.get(i).attributes.DOELGROEP,toilet.features.get(i).attributes.LUIERTAFEL,toilet.features.get(i).attributes.LAT,toilet.features.get(i).attributes.LONG,toilet.features.get(i).attributes.INTEGRAAL_TOEGANKELIJK,toilet.features.get(i).geometry.x,toilet.features.get(i).geometry.y))
                        }
                        adapter = OverviewAdapter(attributesList, lastKnownLocation)
                    }
                    override fun onFailure(call: okhttp3.Call, e: IOException) {
                        println("Get json fail")
                    }
                })
            }
        }
        return attributesList
    }*/

    fun setAdapter(attributesList: MutableList<Attributes>) {
        adapter = OverviewAdapter(attributesList, lastKnownLocation)
        recyclerView.adapter = adapter
        println(adapter.itemCount)
    }

    private fun intialiseViews(view: View){
        filterMen = view.findViewById(R.id.filter_men)
        filterWoman = view.findViewById(R.id.filter_woman)
        filterWheelChair = view.findViewById(R.id.filter_wheelchair)
        filterChangingTable = view.findViewById(R.id.filter_changing_table)
    }

    fun addEventHandlers(){
        filterMen.setOnClickListener {
            if(filterMen.isChecked){
                list = attributesList.filter {x -> x.DOELGROEP == "man/vrouw" || x.DOELGROEP == "man"}
                adapter = OverviewAdapter(list, lastKnownLocation)
                recyclerView.adapter = adapter
            } else {
                adapter = OverviewAdapter(attributesList, lastKnownLocation)
                recyclerView.adapter = adapter
            }
        }
        filterWoman.setOnClickListener {
            if(filterWoman.isChecked){
                list = attributesList.filter { x -> x.DOELGROEP == "man/vrouw" || x.DOELGROEP == "vrouw" }
                adapter = OverviewAdapter(list, lastKnownLocation)
                recyclerView.adapter = adapter
            } else {
                adapter = OverviewAdapter(attributesList, lastKnownLocation)
                recyclerView.adapter = adapter
            }
        }
        filterWheelChair.setOnClickListener {
            if(filterWheelChair.isChecked){
                list = attributesList.filter { x -> x.INTEGRAAL_TOEGANKELIJK == "ja" }
                adapter = OverviewAdapter(list, lastKnownLocation)
                recyclerView.adapter = adapter
            } else {
                adapter = OverviewAdapter(attributesList, lastKnownLocation)
                recyclerView.adapter = adapter
            }
        }
        filterChangingTable.setOnClickListener {
            if(filterChangingTable.isChecked){
                list = attributesList.filter { x -> x.LUIERTAFEL == "ja" }
                adapter = OverviewAdapter(list, lastKnownLocation)
                recyclerView.adapter = adapter
            } else {
                adapter = OverviewAdapter(attributesList, lastKnownLocation)
                recyclerView.adapter = adapter
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
        }
        getDeviceLocation()
    }

    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this.requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                    } else {
                        Log.d(ContentValues.TAG, "Current location is null. Using defaults.")
                        Log.e(ContentValues.TAG, "Exception: %s", task.exception)
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
}