package org.mabartos.meetmethere.ui.list

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
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
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.maps.android.clustering.ClusterManager
import org.mabartos.meetmethere.R
import org.mabartos.meetmethere.data.event.EventsListItem
import org.mabartos.meetmethere.databinding.FragmentEventListBinding
import org.mabartos.meetmethere.service.event.EventService
import org.mabartos.meetmethere.service.event.EventServiceUtil
import org.mabartos.meetmethere.util.ClusterEventItem
import org.mabartos.meetmethere.util.formatDate
import org.mabartos.meetmethere.util.formatTime
import kotlin.math.hypot


class EventsListFragment(
    private val eventService: EventService = EventServiceUtil.createService()
) : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: FragmentEventListBinding
    private lateinit var events: List<EventsListItem>

    private var isMarkerSelected = false
    private var clusterManager: ClusterManager<ClusterEventItem>? = null

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

        binding.eventSelected.eventCardLayout.background =
            ResourcesCompat.getDrawable(resources, R.color.freesia, null)
        binding.eventSelected.eventImage.background =
            ResourcesCompat.getDrawable(resources, R.color.tiffany_blue, null)

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
        mMap.uiSettings.isZoomControlsEnabled = true

        if (clusterManager == null) {
            clusterManager = ClusterManager(context, mMap)
        }

        clusterManager!!.clearItems()

        clusterManager!!.setOnClusterItemClickListener { false }
        clusterManager!!.setOnClusterClickListener { false }
        mMap.setOnCameraIdleListener(clusterManager)
        mMap.setOnMarkerClickListener(clusterManager)

        clusterManager!!.setOnClusterItemClickListener { i -> onMarkerClick(i) }
        mMap.setOnMapClickListener(this)

        val markers = mutableListOf<ClusterEventItem>()

        events.forEach {
            val marker = ClusterEventItem(
                it.latitude,
                it.longitude,
                it.title,
                context?.formatDate("dd.MM - ", it.startTime) +
                        context?.formatTime(it.startTime.toLocalTime()),
                it
            )
            markers.add(marker)
            clusterManager!!.addItem(marker)
        }

        val builder = LatLngBounds.Builder()

        for (marker in markers) {
            builder.include(marker.position)
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

    private fun onMarkerClick(eventItem: ClusterEventItem): Boolean {
        isMarkerSelected = true
        val event = eventItem.event

        EventsViewHolder(binding.eventSelected).bind(event, onItemClick = {
            findNavController()
                .navigate(EventsListFragmentDirections.actionListFragmentToDetailFragment(event.id))
        })

        changeVisibility(View.VISIBLE)
        return false
    }

    override fun onMapClick(coordinates: LatLng) {
        if (isMarkerSelected) {
            changeVisibility(View.GONE)
            isMarkerSelected = false
        }
    }

    private fun changeVisibility(visibility: Int) {
        val cx: Int = binding.eventSelectedEventSection.width / 2
        val cy: Int = binding.eventSelectedEventSection.height / 2

        val maxRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()

        val startRadius = if (visibility == View.VISIBLE) 0f else maxRadius
        val finalRadius = if (visibility == View.VISIBLE) maxRadius else 0f

        val anim = ViewAnimationUtils.createCircularReveal(
            binding.eventSelectedEventSection,
            cx,
            cy,
            startRadius,
            finalRadius
        )

        if (visibility == View.VISIBLE) {
            binding.eventSelectedEventSection.visibility = visibility
        } else {
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    binding.eventSelectedEventSection.visibility = View.GONE
                }
            })
        }
        anim.start()
    }

}