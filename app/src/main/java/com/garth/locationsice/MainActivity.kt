package com.garth.locationsice

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import com.google.gson.Gson
import java.io.IOError
import java.io.IOException
import java.net.URL
import java.util.Locale
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), LocationListener {
    private lateinit var locationManager: LocationManager
    private lateinit var locationTextView: TextView
    private var locationPermissionCode = 1
    private lateinit var currentLocation: Location
    private lateinit var placesListView: ListView
    private lateinit var placeAdapter: PlaceAdapter
    private val places: MutableList<Feature> = mutableListOf()
    private val categories = arrayOf("Catering", "Entertainment", "Pet")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val button: Button = findViewById(R.id.btnFind)
        val categoryList = findViewById<Spinner>(R.id.categories)

        val adapter = ArrayAdapter(this, R.layout.spinner_item, categories)
        categoryList.adapter = adapter

        button.setOnClickListener{
            getPlaces(categoryList.selectedItem.toString().toLowerCase())
        }

        getLocation()
    }
    private fun getLocation(){
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if((ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ){
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode
            )
        } else{
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000, 5f, this
            )
        }
    }
    override fun onLocationChanged(location: Location){
        currentLocation = location
        getAddressFromLocation(location)
    }

    private fun getAddressFromLocation(location: Location){
        val geocoder = Geocoder(this, Locale.getDefault())
        locationTextView = findViewById(R.id.lblLocation)
        try{
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if(addresses != null && addresses.isNotEmpty()){
                val address = addresses[0]
                val addressLine = address.getAddressLine(0)
                locationTextView.text = "Address: $addressLine"
            }else {
                locationTextView.text = "unable to get address"
            }
        }
        catch (e: IOException){
            e.printStackTrace()
            locationTextView.text = "Error getting address"
        }
    }
    private fun getPlaces(category: String){
        val executor = Executors.newSingleThreadExecutor()
        val placesListView = findViewById<ListView>(R.id.items)
        executor.execute {
            try {
                val url = URL("https://api.geoapify.com/v2/places?categories=${category}&bias=proximity:${currentLocation.longitude},${currentLocation.latitude}&limit=20&apiKey=1d71a3ed16f4429a9bb828d8e1e04d8b")
                val json = url.readText()
                if(json.equals("null")){
                    Log.d("Error", "Nothing found")
                }
                else{
                    val placesResponse = Gson().fromJson(json, Response::class.java)
                    Handler(Looper.getMainLooper()).post{
                        places.clear()
                        places.addAll(placesResponse.features)
                        placeAdapter = PlaceAdapter(this, places)
                        placesListView.adapter = placeAdapter
                    }
                }
            } catch (e: Exception) {
                Log.d("Error", "Fetch error occured")
            }
        }
    }
}