package com.android.mfcolak.googlemapskotlin

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.android.mfcolak.googlemapskotlin.databinding.ActivityMapsBinding
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
private lateinit var binding: ActivityMapsBinding

private lateinit var locationManager: LocationManager
private lateinit var locationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

     binding = ActivityMapsBinding.inflate(layoutInflater)
     setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(listener)
        // Latitude = enlem, Longitude = boylam
        // Add a marker in LatLng move the camera

      /*  val baskent = LatLng(39.923861, 32.854981)
        mMap.addMarker(MarkerOptions().position(baskent).title("Marker in Ankara"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(baskent, 15f))
       */
        //casting -> "as"
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = LocationListener { location ->
            mMap.clear()
            val currentLocation = LatLng(location.latitude, location.longitude)
            mMap.addMarker(MarkerOptions().position(currentLocation).title("Güncel Konumunuz"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))

            val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
            try {

                val adressList = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (adressList.size >0){
                    println(adressList.get(0))
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }


        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
            //izin verilmiş
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f, locationListener)
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnownLocation != null){

                val lastKnownLatLng = LatLng(lastKnownLocation.latitude,lastKnownLocation.longitude)
                mMap.addMarker(MarkerOptions().position(lastKnownLatLng).title("En Son Görünen Konumunuz"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLatLng, 15f))

            }

        }else{
            //izin verilmemiş
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
        }

    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1){
            if (grantResults.size > 0){
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    //izin verilmiş
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f, locationListener)

                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    val listener =object: GoogleMap.OnMapLongClickListener {
        override fun onMapLongClick(p0: LatLng) {
            mMap.clear()
            val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())


                var adress = ""
                try {

                    val adresslist = geocoder.getFromLocation(p0.latitude, p0.longitude, 1)

                    if (adresslist.size > 0 ) {
                        if (adresslist[0].thoroughfare != null) {
                          adress +=  adresslist[0].thoroughfare
                            if (adresslist[0].subThoroughfare != null){

                                adress += adresslist[0].subThoroughfare
                             }
                        }
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }

            mMap.addMarker(MarkerOptions().position(p0).title(adress))

        }

    }

}