package com.example.greatingcard.screen.main.order.checkout

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.example.greatingcard.R
import com.example.greatingcard.databinding.ActivityMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class MapActivity : FragmentActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the map fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Set up the search functionality
        binding.svLocation.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val location = query?.trim()
                if (!location.isNullOrEmpty()) {
                    searchLocation(location)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // Handle the confirm button click
        binding.btnConfirmLocation.setOnClickListener {
            confirmLocation()
        }

        // Handle the my location button click
        binding.btnMyLocation.setOnClickListener {
            getCurrentLocation()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener { latLng ->
            placeMarker(latLng)
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                placeMarker(currentLatLng)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            } ?: run {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchLocation(location: String) {
        val geocoder = Geocoder(this)
        try {
            val addressList: List<Address> = geocoder.getFromLocationName(location, 1) ?: emptyList()
            if (addressList.isNotEmpty()) {
                val address = addressList[0]
                val latLng = LatLng(address.latitude, address.longitude)
                placeMarker(latLng)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
        }
    }

    private fun placeMarker(latLng: LatLng) {
        currentMarker?.remove()
        val markerOptions = MarkerOptions().position(latLng)
        currentMarker = mMap.addMarker(markerOptions)
        currentMarker?.showInfoWindow()
    }

    private fun confirmLocation() {
        currentMarker?.let {
            val geocoder = Geocoder(this)
            try {
                val addressList: List<Address> = geocoder.getFromLocation(it.position.latitude, it.position.longitude, 1)?.filterNotNull() ?: emptyList()
                if (addressList.isNotEmpty()) {
                    val address = addressList[0].getAddressLine(0)
                    val resultIntent = Intent()
                    resultIntent.putExtra("selected_address", address)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                } else {
                    Toast.makeText(this, "Unable to get address", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Unable to get address again", Toast.LENGTH_SHORT).show()
            }
        } ?: Toast.makeText(this, "No location selected", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
