package org.mabartos.meetmethere.service.map

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import org.mabartos.meetmethere.data.event.EventsListItem
import org.mabartos.meetmethere.util.ClusterEventItem

abstract class MapProvider(private val fragment: Fragment) {
    protected var lastLocation: Location? = null
    protected var locationPermissionGranted = false
    var isMarkerSelected = false
    protected var onMarkerClick: ((ClusterEventItem) -> Boolean?)? = null
    protected var onMapClick: ((LatLng) -> Unit)? = null

    abstract fun getLocationPermission()

    abstract fun updateLocationUI()

    abstract fun getDeviceLocation()

    abstract fun onStop()

    abstract fun getMarkers(initMethod: () -> List<ClusterEventItem>)

    open fun onMarkerClick(
        onMarkerClick: (ClusterEventItem) -> Boolean
    ) {
        isMarkerSelected = true
        this.onMarkerClick = onMarkerClick
    }

    open fun onMapClick(onMapClick: (LatLng) -> Unit) {
        this.onMapClick = onMapClick
    }

    open fun init(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            lastLocation = savedInstanceState.getParcelable(KEY_LOCATION)
        }
    }

    fun getLastKnownLocation(): Location? = lastLocation

    open fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(KEY_LOCATION, lastLocation)
    }

    fun requestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
            else -> fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        updateLocationUI()
    }

    companion object {
        val TAG = MapProvider::class.java.simpleName
        const val DEFAULT_ZOOM = 15
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        // Keys for storing activity state.
        const val KEY_CAMERA_POSITION = "camera_position"
        const val KEY_LOCATION = "location"

        // Used for selecting the current place.
        const val M_MAX_ENTRIES = 5
    }
}