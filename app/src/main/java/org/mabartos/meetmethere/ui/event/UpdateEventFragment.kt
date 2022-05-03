package org.mabartos.meetmethere.ui.event

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.mabartos.meetmethere.R
import org.mabartos.meetmethere.data.Event
import org.mabartos.meetmethere.databinding.FragmentEventUpdateBinding
import org.mabartos.meetmethere.util.*
import org.mabartos.meetmethere.webservice.EventsApi
import org.mabartos.meetmethere.webservice.RetrofitUtil
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

class UpdateEventFragment(
    private val eventsApi: EventsApi = RetrofitUtil.createAqiWebService()
) : Fragment() {

    private lateinit var binding: FragmentEventUpdateBinding
    private var startDate: Calendar? = null
    private var endDate: Calendar? = null
    private var startTime: LocalTime? = null
    private var endTime: LocalTime? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventUpdateBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.eventUpdateToolbar.setNavigationIcon(R.drawable.ic_close)
        binding.eventUpdateToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.eventUpdateToolbar.title = "Update event"
        var isUpdated = false

        val event = UpdateEventFragmentArgs.fromBundle(requireArguments()).event

        binding.eventUpdateName.hint = event.title
        binding.eventUpdateVenue.hint = event.venue
        binding.eventUpdateDescription.hint = event.description

        binding.eventUpdateStartDay.hint = context?.formatDate("dd.MM", event.startTime)
        binding.eventUpdateStartTime.hint = context?.formatTime(event.startTime.toLocalTime())
        binding.eventUpdateEndDay.hint = context?.formatDate("dd.MM", event.endTime)
        binding.eventUpdateEndTime.hint = context?.formatTime(event.endTime.toLocalTime())

        binding.eventUpdateStartDayInput.setOnClickListener {
            context?.datePicker(
                parentFragmentManager,
                title = R.string.start_day_settings.toString(),
                onPositiveClick = { calendar ->
                    startDate = calendar
                    binding.eventUpdateStartDay.hint =
                        context?.formatDate("dd.MM", calendar.time)
                }
            )
        }

        binding.eventUpdateStartTimeInput.setOnClickListener {
            context?.timePicker(
                parentFragmentManager,
                requireContext(),
                title = "Set start time",
                onPositiveClick = { time ->
                    startTime = time
                    binding.eventUpdateStartTime.hint = context?.formatTime(time)
                })
        }

        binding.eventUpdateEndDayInput.setOnClickListener {
            context?.datePicker(
                parentFragmentManager,
                title = R.string.end_day_settings.toString(),
                onPositiveClick = { calendar ->
                    endDate = calendar
                    binding.eventUpdateEndDay.hint =
                        context?.formatDate("dd.MM", calendar.time)
                }
            )
        }

        binding.eventUpdateEndTimeInput.setOnClickListener {
            context?.timePicker(
                parentFragmentManager,
                requireContext(),
                title = "Set end time",
                onPositiveClick = { time ->
                    endTime = time
                    binding.eventUpdateEndTime.hint = context?.formatTime(time)
                })
        }


        binding.eventUpdateSave.setOnClickListener {
            val titleText = binding.eventUpdateNameInput.text.toString()
            isUpdated = isUpdated or (titleText !== binding.eventUpdateName.hint.toString())

            val venueText = binding.eventUpdateVenueInput.text.toString()
            isUpdated = isUpdated or (venueText !== binding.eventUpdateName.hint.toString())

            val descriptionText = binding.eventUpdateDescriptionInput.text.toString()
            isUpdated =
                isUpdated or (descriptionText !== binding.eventUpdateDescription.hint.toString())

            //TODO
            if (isUpdated) {
                val updatedEvent =
                    Event(
                        title = titleText,
                        venue = venueText,
                        description = descriptionText,
                        isPublic = true,
                        imageUrl = "some",
                        longitude = 21.0,
                        latitude = 23.0,
                        startTime = LocalDateTime.of(2022, 10, 20, 20, 0),
                        endTime = LocalDateTime.of(2022, 10, 20, 19, 0)
                    )

                context?.response(supplier = { eventsApi.updateEvent(event.id, updatedEvent) },
                    onSuccess = { id: Long ->
                        context?.toast("OK - updated ${id}")
                    },
                    onFailure = { e ->
                        context?.toast("Cannot update event")
                    })
            }
        }

        //TODO time


    }
}