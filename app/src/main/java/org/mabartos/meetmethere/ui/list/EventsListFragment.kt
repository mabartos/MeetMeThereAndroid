package org.mabartos.meetmethere.ui.list

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.mabartos.meetmethere.R
import org.mabartos.meetmethere.databinding.FragmentEventListBinding
import org.mabartos.meetmethere.repository.EventsRepository


class EventsListFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: FragmentEventListBinding

    private var currentPositionMarker: MarkerOptions? = null
    private var currentLocationMarker: Marker? = null

    private val eventsRepository: EventsRepository by lazy {
        EventsRepository(requireContext())
    }

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = EventsListAdapter(
            onItemClick = {
                findNavController()
                    .navigate(EventsListFragmentDirections.actionListFragmentToDetailFragment(it.id.toString()))
            },
        )

        binding.eventsList.layoutManager = LinearLayoutManager(requireContext())
        binding.eventsList.adapter = adapter


        adapter.submitList(eventsRepository.getMockedData())
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val brno = LatLng(49.20, 16.58)
        updateCurrentLocationMarker(brno, "Event with friends")
    }

    private fun updateCurrentLocationMarker(latLng: LatLng, eventName: String) {
        if (currentPositionMarker == null) {
            currentPositionMarker = MarkerOptions()
            currentPositionMarker!!.position(latLng)
                .title(eventName)
            currentLocationMarker = mMap.addMarker(currentPositionMarker!!)
        }
        if (currentLocationMarker != null) currentLocationMarker!!.position = latLng

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

}