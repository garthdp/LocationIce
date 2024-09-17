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
import java.io.IOError
import java.io.IOException
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var locationManager: LocationManager
    private lateinit var  tvOutput: TextView
    private lateinit var locationTextView: TextView
    private var locationPermissionCode = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val button: Button = findViewById(R.id.btnFind)

        button.setOnClickListener {
            getLocation()
        }
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
        tvOutput = findViewById(R.id.lblLocation)
        tvOutput.text = "Latitude: " + location.latitude + ", \nLongitude: " + location.longitude
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
}