package com.example.locationsexamples

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var googleMap: GoogleMap
    lateinit var currentLocationLatLng: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestLocationPermissions()
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun requestLocationPermissions() {
        val listOfPermissions = arrayListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val isPermissionGranted = listOfPermissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (isPermissionGranted.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this, listOfPermissions.toTypedArray(), PERMISSION_REQUEST_CODE
            )
        } else {
            fetchLocations()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                fetchLocations()
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun fetchLocations() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            currentLocationLatLng = LatLng(location.latitude, location.longitude)
            if (::googleMap.isInitialized) {
                googleMap.addMarker(
                    MarkerOptions().position(currentLocationLatLng).title("current Location")
                )
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLatLng, 15f))
            }
        }.addOnFailureListener {
            Log.d("Current Location", "fetchLocations: ${it.message}")
        }
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 100
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }
}