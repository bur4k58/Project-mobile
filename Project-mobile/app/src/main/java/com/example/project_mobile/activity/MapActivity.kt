package com.example.project_mobile.activity

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.example.project_mobile.R
import com.example.project_mobile.adapters.OverviewAdapter
import com.example.project_mobile.data.Attributes
import com.example.project_mobile.data.JsonBase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.*
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import java.io.File
import java.io.IOException
import java.net.URL
import java.net.URLEncoder
import java.util.ArrayList

class MapActivity : AppCompatActivity() {
    private lateinit var mMapView: MapView
    private var mMyLocationOverlay: ItemizedOverlay<OverlayItem>? = null
    private var items = ArrayList<OverlayItem>()
    private var searchField: EditText? = null
    private var searchButton: Button? = null
    private var clearButton: Button? = null
    private val urlNominatim = "https://nominatim.openstreetmap.org/"
    private var notificationManager: NotificationManager? = null
    private var mChannel: NotificationChannel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val osmConfig = Configuration.getInstance()
        osmConfig.userAgentValue = packageName
        val basePath = File(cacheDir.absolutePath, "osmdroid")
        osmConfig.osmdroidBasePath = basePath
        val tileCache = File(osmConfig.osmdroidBasePath, "tile")
        osmConfig.osmdroidTileCache = tileCache

        setContentView(R.layout.activity_map)
        mMapView = findViewById(R.id.mapview)

        searchField = findViewById(R.id.search_txtview)
        searchButton = findViewById(R.id.search_button)
        searchButton?.setOnClickListener {
            val url = URL(urlNominatim + "search?q=" + URLEncoder.encode(searchField?.text.toString(), "UTF-8") + "&format=json")
            it.hideKeyboard()
            getAddressOrLocation(url)
        }

        clearButton = findViewById(R.id.clear_button)
        clearButton?.setOnClickListener {
            mMapView?.overlays?.clear()
            // Redraw map
            mMapView?.invalidate()
        }

        // Permissions
        if (hasPermissions()) {
            initMap()
        }
        else {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION), 100)
        }
        // Notifications
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mChannel = NotificationChannel("my_channel_01","My Channel", NotificationManager.IMPORTANCE_HIGH)
        mChannel?.setShowBadge(true)

        notificationManager?.createNotificationChannel(mChannel!!)

        //Bottom navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navi)
        bottomNavigationView.selectedItemId = R.id.map
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.map -> return@OnNavigationItemSelectedListener true
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

    private fun hasPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (hasPermissions()) {
                initMap()
            } else {
                finish()
            }
        }
    }

    private fun initMap() {
        mMapView?.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        // add receiver to get location from tap
        val mReceive: MapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                val url = URL(urlNominatim + "reverse?lat=" + p.latitude.toString() + "&lon=" + p.longitude.toString() + "&format=json")
                getAddressOrLocation(url)
                return false
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }
        mMapView?.overlays?.add(MapEventsOverlay(mReceive))

        mMapView?.controller?.setZoom(20.0)
        setCenter(GeoPoint(51.21989 , 4.40346), "Antwerpen")
        val database = Firebase.database
        addMarkerDB(database)


    }
    private fun addMarkerDB(database: FirebaseDatabase)
    {
        database.getReference("toilets").get().addOnSuccessListener {
            if(it.value != null)
            {
                for (jobSnapshot in it.getChildren())
                {
                    val toilet: Attributes? = jobSnapshot.getValue(Attributes::class.java)
                    if (toilet != null) {
                        val geoPoint = GeoPoint(toilet.y,toilet.x)
                        val startMarker = Marker(mMapView)
                        startMarker.position = geoPoint
                        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        startMarker.title= "${toilet.STRAAT} ${toilet.HUISNUMMER} \n${toilet.DOELGROEP}"
                        if (toilet.LUIERTAFEL=="ja") {
                            startMarker.setIcon(getResources().getDrawable(R.drawable.ic_baseline_baby_changing_station_24));
                        }
                        else if (toilet.INTEGRAAL_TOEGANKELIJK=="ja") {
                            startMarker.setIcon(getResources().getDrawable(R.drawable.ic_baseline_wheelchair_pickup_24));
                        }
                        else if (toilet.DOELGROEP=="man") {
                            startMarker.setIcon(getResources().getDrawable(R.drawable.ic_baseline_man_24));
                        }
                        else if (toilet.DOELGROEP=="man/vrouw") {
                            startMarker.setIcon(getResources().getDrawable(R.drawable.ic_baseline_family_restroom_24));
                        }
                        else if (toilet.DOELGROEP=="man") {
                            startMarker.setIcon(getResources().getDrawable(R.drawable.ic_baseline_man_24));
                        }
                        // 3 null markers, thanks antwerpen
                        else{
                            startMarker.setIcon(getResources().getDrawable(R.drawable.ic_baseline_man_24));
                        }
                        addMarkerIcon(startMarker,toilet.STRAAT.toString())
                    }
                }
            }
        }
    }

    private fun addMarkerIcon(startMarker: Marker, name: String) {
        mMapView?.overlays?.add(startMarker)
        mMapView.invalidate();
    }

    private fun addMarker(geoPoint: GeoPoint, name: String) {
        items.add(OverlayItem(name, name, geoPoint))
        mMyLocationOverlay = ItemizedIconOverlay(items, null, applicationContext)
        mMapView?.overlays?.add(mMyLocationOverlay)
        mMapView.invalidate();
    }

    private fun setCenter(geoPoint: GeoPoint, name: String) {
        mMapView?.controller?.setCenter(geoPoint)
        addMarker(geoPoint, name)
    }

    fun createNotification(iconRes: Int, title: String, body: String, channelId: String) {
        notificationManager?.createNotificationChannel(mChannel!!)
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(iconRes)
            .setContentTitle(title)
            .setContentText(body)
            .build()

        notificationManager?.notify(0, notification)
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    private fun getAddressOrLocation(url : URL) {
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

                    createNotification(R.drawable.ic_menu_compass,
                        "Reverse lookup result",
                        obj.string("display_name")!!,
                        "my_channel_01")
                }
                else {
                    val array = parser.parse(jsonString) as JsonArray<JsonObject>

                    if (array.size > 0) {
                        val obj = array[0]
                        // mapView center must be updated here and not in doInBackground because of UIThread exception
                        val geoPoint = GeoPoint(obj.string("lat")!!.toDouble(), obj.string("lon")!!.toDouble())
                        setCenter(geoPoint, obj.string("display_name")!!)
                    }
                    else {
                        Toast.makeText(applicationContext, "Address not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }).start()
    }
}