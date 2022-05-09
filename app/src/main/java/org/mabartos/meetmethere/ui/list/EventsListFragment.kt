package org.mabartos.meetmethere.ui.list

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import org.mabartos.meetmethere.R
import org.mabartos.meetmethere.data.event.EventsListItem
import org.mabartos.meetmethere.databinding.FragmentEventListBinding
import org.mabartos.meetmethere.service.event.EventService
import org.mabartos.meetmethere.service.event.EventServiceUtil
import org.mabartos.meetmethere.service.map.GoogleMapProvider
import org.mabartos.meetmethere.service.map.MapProvider
import org.mabartos.meetmethere.util.ClusterEventItem
import org.mabartos.meetmethere.util.formatDate
import org.mabartos.meetmethere.util.formatTime
import org.mabartos.meetmethere.util.toast
import java.util.stream.Collectors
import kotlin.math.hypot


class EventsListFragment(
    private val eventService: EventService = EventServiceUtil.createService()
) : Fragment() {

    private lateinit var mapProvider: MapProvider

    private lateinit var binding: FragmentEventListBinding
    private lateinit var events: List<EventsListItem>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventListBinding.inflate(layoutInflater, container, false)

        mapProvider = GoogleMapProvider(requireContext(), this)
        mapProvider.init(savedInstanceState)

        mapProvider.onMarkerClick { eventItem ->
            val event = eventItem.event

            EventsViewHolder(binding.eventSelected, resources).bind(event, onItemClick = {
                findNavController()
                    .navigate(EventsListFragmentDirections.actionListFragmentToDetailFragment(event))
            })

            changeVisibility(View.VISIBLE)
            mapProvider.isMarkerSelected = true
            return@onMarkerClick false
        }

        mapProvider.onMapClick {
            if (mapProvider.isMarkerSelected) {
                changeVisibility(View.GONE)
                mapProvider.isMarkerSelected = false
            }
        }

        mapProvider.getMarkers {
            events.stream()
                .filter { f -> f.latitude != null && f.longitude != null }.map {
                    ClusterEventItem(
                        it.latitude!!,
                        it.longitude!!,
                        it.title,
                        context?.formatDate("dd.MM - ", it.startTime) +
                                context?.formatTime(it.startTime.toLocalTime()),
                        it
                    )
                }.collect(Collectors.toList())
        }

        return binding.root
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        mapProvider.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = EventsListAdapter(
            onItemClick = { event ->
                findNavController()
                    .navigate(EventsListFragmentDirections.actionListFragmentToDetailFragment(event))
            },
        )

        eventService.getEvents(onSuccess = { eventsList ->
            this.events = eventsList
            adapter.submitList(this.events)
        }, onFailure = { e ->
            Log.e(EventsListFragment::getTag.toString(), "Cannot fetch events", e)
        })

        binding.createEventFloatingButton.setOnClickListener {
            findNavController().navigate(EventsListFragmentDirections.actionListFragmentToCreateEventDialog())
        }

        binding.eventSelected.eventCardLayout.background =
            ResourcesCompat.getDrawable(resources, R.color.freesia, null)
        binding.eventSelected.eventImage.background =
            ResourcesCompat.getDrawable(resources, R.color.tiffany_blue, null)

        binding.eventsList.layoutManager = LinearLayoutManager(requireContext())
        binding.eventsList.adapter = adapter

        binding.searchPlaceButton.setOnClickListener {
            val AUTOCOMPLETE_REQUEST_CODE = 1

            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            val fields = listOf(Place.Field.ID, Place.Field.NAME)

            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(requireContext())
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        binding.profileButton.setOnClickListener {
            findNavController().navigate(EventsListFragmentDirections.actionListFragmentToUserProfile())
        }

        binding.logoutButton.setOnClickListener {
            findNavController().navigate(EventsListFragmentDirections.actionListFragmentToLoginFragment())
            context?.toast(resources.getString(R.string.signed_off_note))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i("map", "Place: ${place.name}, ${place.id}")
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i("map", status.statusMessage ?: "")
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStop() {
        super.onStop()
        mapProvider.onStop()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        mapProvider.requestPermissionsResult(requestCode, permissions, grantResults)
    }

}