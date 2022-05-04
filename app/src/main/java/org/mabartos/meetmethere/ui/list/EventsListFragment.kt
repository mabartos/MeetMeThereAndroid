package org.mabartos.meetmethere.ui.list

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import org.mabartos.meetmethere.R
import org.mabartos.meetmethere.data.event.EventsListItem
import org.mabartos.meetmethere.databinding.FragmentEventListBinding
import org.mabartos.meetmethere.service.event.EventService
import org.mabartos.meetmethere.service.event.EventServiceUtil

class EventsListFragment(
    private val eventService: EventService = EventServiceUtil.createService()
) : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: FragmentEventListBinding
    private lateinit var events: List<EventsListItem>

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private var cancellationTokenSource = CancellationTokenSource()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventListBinding.inflate(layoutInflater, container, false)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.event_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.events = eventService.getEvents()

        requestCurrentLocation()

        val adapter = EventsListAdapter(
            onItemClick = { event ->
                findNavController()
                    .navigate(EventsListFragmentDirections.actionListFragmentToDetailFragment(event.id))
            },
        )

        binding.createEventFloatingButton.setOnClickListener {
            findNavController().navigate(EventsListFragmentDirections.actionListFragmentToCreateEventDialog())
        }

        binding.eventsList.layoutManager = LinearLayoutManager(requireContext())
        binding.eventsList.adapter = adapter

        adapter.submitList(this.events)

        binding.logoutButton.setOnClickListener {
            findNavController().navigate(EventsListFragmentDirections.actionListFragmentToLoginFragment())
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMyLocationButtonEnabled = true

        val markers = mutableListOf<Marker?>()

        events.forEach {
            val options =
                MarkerOptions().position(LatLng(it.latitude, it.longitude)).title(it.title)
            markers.add(mMap.addMarker(options))
        }

        val builder = LatLngBounds.Builder()

        for (marker in markers) {
            if (marker != null) {
                builder.include(marker.position)
            }
        }

        val bounds = builder.build()
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (height * 0.20).toInt()

        val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)

        mMap.animateCamera(cu);
    }

    override fun onStop() {
        super.onStop()
        // Cancels location request (if in flight).
        cancellationTokenSource.cancel()
    }

    private fun requestCurrentLocation() {
        // Check Fine permission
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {

            // Main code
            val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
                PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            )

            currentLocationTask.addOnCompleteListener { task: Task<Location> ->
                val result = if (task.isSuccessful) {
                    val result: Location = task.result
                    "Location (success): ${result.latitude}, ${result.longitude}"
                } else {
                    val exception = task.exception
                    "Location (failure): $exception"
                }

                Log.d(TAG, "getCurrentLocation() result: $result")
            }
        }
    }

}