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
                setAdapter(list as MutableList<Attributes>)
            } else {
                adapter = OverviewAdapter(attributesList, lastKnownLocation)
                recyclerView.adapter = adapter
            }
        }
        filterWoman.setOnClickListener {
            if(filterWoman.isChecked){
                list = attributesList.filter { x -> x.DOELGROEP == "man/vrouw" || x.DOELGROEP == "vrouw" }
                setAdapter(list as MutableList<Attributes>)
            } else {
                adapter = OverviewAdapter(attributesList, lastKnownLocation)
                recyclerView.adapter = adapter
            }
        }
        filterWheelChair.setOnClickListener {
            if(filterWheelChair.isChecked){
                list = attributesList.filter { x -> x.INTEGRAAL_TOEGANKELIJK == "ja" }
                setAdapter(list as MutableList<Attributes>)
            } else {
                adapter = OverviewAdapter(attributesList, lastKnownLocation)
                recyclerView.adapter = adapter
            }
        }
        filterChangingTable.setOnClickListener {
            if(filterChangingTable.isChecked){
                list = attributesList.filter { x -> x.LUIERTAFEL == "ja" }
                setAdapter(list as MutableList<Attributes>)
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