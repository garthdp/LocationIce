package com.garth.locationsice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class PlaceAdapter(context: Context, private val places: List<Feature>) : ArrayAdapter<Feature>(context, 0, places) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val place = getItem(position)

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_place, parent, false)

        val nameTextView: TextView = view.findViewById(R.id.place_name)
        val addressTextView: TextView = view.findViewById(R.id.place_address)

        nameTextView.text = place?.properties?.name ?: "Unknown Place"
        addressTextView.text = place?.properties?.address_line2 ?: "No Address"

        return view
    }
}