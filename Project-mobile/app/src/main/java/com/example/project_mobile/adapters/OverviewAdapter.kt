package com.example.project_mobile.adapters

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_mobile.R
import com.example.project_mobile.data.Attributes

class OverviewAdapter(private val dataSet: List<Attributes>, private val location: Location) : RecyclerView.Adapter<OverviewAdapter.ViewHolder>() {

    private var distanceInMeters: Double = 0.0

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

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        var stringBuilder: StringBuilder = StringBuilder()
        val attributes: Attributes = dataSet[position]

        viewHolder.textOmschrijving.text = attributes.OMSCHRIJVING
        stringBuilder.append(attributes.DISTRICT + " ")
        stringBuilder.append(attributes.POSTCODE.toString())
        viewHolder.textDistrict.text = stringBuilder
        viewHolder.textStraat.text = attributes.STRAAT


        val loc2 = Location("")
        loc2.latitude = attributes.y.toDouble()
        loc2.longitude = attributes.x.toDouble()

        distanceInMeters = location.distanceTo(loc2).toDouble()
        distanceInMeters /= 100000

        val distanceToString = String.format("%.2f km", distanceInMeters)
        viewHolder.textDistance.text = distanceToString
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}