package org.mabartos.meetmethere.ui.event

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import org.mabartos.meetmethere.R
import org.mabartos.meetmethere.data.Event
import org.mabartos.meetmethere.databinding.FragmentEventCreateBinding
import org.mabartos.meetmethere.util.datePicker
import org.mabartos.meetmethere.util.response
import org.mabartos.meetmethere.util.toast
import org.mabartos.meetmethere.webservice.EventsApi
import org.mabartos.meetmethere.webservice.RetrofitUtil
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class CreateEventFragment(
    private val eventsApi: EventsApi = RetrofitUtil.createAqiWebService()
) : Fragment() {

    private lateinit var binding: FragmentEventCreateBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventCreateBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.eventCreateToolbar.setNavigationIcon(R.drawable.ic_close)
        binding.eventCreateToolbar.setCollapseIcon(R.drawable.ic_burger_menu)
        binding.eventCreateToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.eventCreateToolbar.title = "Create event"

        binding.eventCreateStartDayInput.setOnClickListener {
            context?.datePicker<Long>(
                parentFragmentManager,
                title = R.string.start_day_settings.toString(),
                onPositiveClick = { selection ->
                    val outputDateFormat = SimpleDateFormat("dd.MM", Locale.getDefault())
                    binding.eventCreateStartDay.hint = outputDateFormat.format(selection)
                }
            )
        }

        //TODO
        binding.eventCreateSaveButton.setOnClickListener {
            val titleText = binding.eventCreateNameInput.text.toString()
            val venueText = binding.eventCreateVenueInput.text.toString()
            val descriptionText = binding.eventCreateDescriptionInput.text.toString()

            val createEvent =
                Event(
                    title = titleText,
                    venue = venueText,
                    imageUrl = "\\TODO",
                    description = descriptionText,
                    startTime = LocalDateTime.of(2022, 10, 15, 20, 0),
                    endTime = LocalDateTime.of(2022, 10, 15, 20, 30),
                    isPublic = true,
                    longitude = 20.0,
                    latitude = 20.0
                )

            context?.response(
                { eventsApi.createEvent(createEvent) },
                onSuccess = { id: Long ->
                    context?.toast("Event created ${id}")
                },
                onFailure = { e: Throwable ->
                    println("Cannot create event. ${e.message}")
                })
        }

    }

}