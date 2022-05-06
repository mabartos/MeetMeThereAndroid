package org.mabartos.meetmethere.service.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.clustering.ClusterManager
import org.mabartos.meetmethere.BuildConfig
import org.mabartos.meetmethere.R
import org.mabartos.meetmethere.util.ClusterEventItem

class GoogleMapProvider(val context: Context, val fragment: Fragment) : MapProvider(fragment),
    OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private lateinit var map: GoogleMap
    private var cameraPosition: CameraPosition? = null
    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var clusterManager: ClusterManager<ClusterEventItem>? = null

    private var cancellationTokenSource = CancellationTokenSource()
    private var getMarkersCallback: (() -> List<ClusterEventItem>)? = null

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        if (savedInstanceState != null) {
            cameraPosition =
                savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }

        Places.initialize(context, BuildConfig.MAPS_API_KEY)
        placesClient = Places.createClient(context)

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        val mapFragment =
            fragment.childFragmentManager.findFragmentById(R.id.event_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_LOCATION, lastLocation)
    }

    @SuppressLint("MissingPermission")
    override fun updateLocationUI() {
        try {
            if (locationPermissionGranted) {
                map.isMyLocationEnabled = true
                map.uiSettings.isMyLocationButtonEnabled = true
            } else {
                map.isMyLocationEnabled = false
                map.uiSettings.isMyLocationButtonEnabled = false
                lastLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    override fun onStop() {
        cancellationTokenSource.cancel()
    }

    override fun onMapClick(location: LatLng) {
        onMapClick?.invoke(location)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isMyLocationButtonEnabled = true
        map.uiSettings.isZoomControlsEnabled = true

        getLocationPermission()

        updateLocationUI()

        getDeviceLocation()

        if (clusterManager == null) {
            clusterManager = ClusterManager(context, map)
        }

        clusterManager!!.clearItems()

        clusterManager!!.setOnClusterItemClickListener { false }
        clusterManager!!.setOnClusterClickListener { false }
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)

        clusterManager!!.setOnClusterItemClickListener { i -> onMarkerClick?.invoke(i)!! }
        map.setOnMapClickListener(this)

        val markers = mutableListOf<ClusterEventItem>()

        val markerList = getMarkersCallback?.invoke()
        markerList?.forEach { marker ->
            markers.add(marker)
            clusterManager!!.addItem(marker)
        }

        val builder = LatLngBounds.Builder()

        for (marker in markers) {
            builder.include(marker.position)
        }

        val bounds = builder.build()
        val width = fragment.resources.displayMetrics.widthPixels
        val height = fragment.resources.displayMetrics.heightPixels
        val padding = (height * 0.20).toInt()

        val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)

        map.animateCamera(cu);
    }

    override fun getMarkers(initMethod: () -> List<ClusterEventItem>) {
        getMarkersCallback = initMethod
    }

    @SuppressLint("MissingPermission")
    override fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(fragment.requireActivity()) { task ->
                    if (task.isSuccessful) {
                        lastLocation = task.result
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        map.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    override fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                fragment.requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }


}