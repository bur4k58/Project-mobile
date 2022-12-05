package com.example.project_mobile.adapters

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.project_mobile.R
import com.example.project_mobile.data.Attributes
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener

class OverviewAdapter(private val dataSet: List<Attributes>, private val location: Location) : RecyclerView.Adapter<OverviewAdapter.ViewHolder>() {

    private var distanceInMeters: Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    var context: Context? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textOmschrijving : TextView = view.findViewById(R.id.textOmschrijving)
        val textDistrict : TextView = view.findViewById(R.id.textDistrict)
        val textStraat : TextView = view.findViewById(R.id.textStraat)
        val textDistance : TextView = view.findViewById(R.id.textDistance)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.fragment_overview_item, viewGroup, false)

        return ViewHolder(view)
    }
    private fun hasPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.context!!, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        var stringBuilder: StringBuilder = StringBuilder()
        val attributes: Attributes = dataSet[position]

        viewHolder.textOmschrijving.text = attributes.OMSCHRIJVING
        if(attributes.DISTRICT != null){
            stringBuilder.append(attributes.DISTRICT + " ")
        }
        if(attributes.POSTCODE != 0){
            stringBuilder.append(attributes.POSTCODE.toString())
        }

        viewHolder.textDistrict.text = stringBuilder
        viewHolder.textStraat.text = attributes.STRAAT


        val loc2 = Location("")
        loc2.latitude = attributes.y
        loc2.longitude = attributes.x
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context!!)

        fusedLocationClient.getCurrentLocation(LocationRequest.QUALITY_HIGH_ACCURACY, object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token

            override fun isCancellationRequested() = false
        })
            .addOnSuccessListener { location: Location? ->
                if (location != null){
                    distanceInMeters = location.distanceTo(loc2).toDouble()
                    distanceInMeters /= 1000

                    val distanceToString = String.format("%.2f km", distanceInMeters)
                    viewHolder.textDistance.text = distanceToString
                }
                else {
                    viewHolder.textDistance.text = "?km"
                }
            }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}