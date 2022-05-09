package org.mabartos.meetmethere.ui.event

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.mabartos.meetmethere.R
import org.mabartos.meetmethere.data.event.Event
import org.mabartos.meetmethere.databinding.FragmentEventUpdateBinding
import org.mabartos.meetmethere.service.event.EventService
import org.mabartos.meetmethere.service.event.EventServiceUtil
import org.mabartos.meetmethere.util.*
import java.time.LocalTime
import java.util.*

class UpdateEventFragment(
    private val eventService: EventService = EventServiceUtil.createService()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.eventUpdateToolbar.setNavigationIcon(R.drawable.ic_close)
        binding.eventUpdateToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.eventUpdateToolbar.title = "Update event"

        val event = UpdateEventFragmentArgs.fromBundle(requireArguments()).event

        binding.eventUpdateTitleInput.setText(event.title)
        binding.eventUpdateVenueInput.setText(event.venue)
        binding.eventUpdateDescriptionInput.setText(event.description)

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
                title = "Set end time",
                onPositiveClick = { time ->
                    endTime = time
                    binding.eventUpdateEndTime.hint = context?.formatTime(time)
                })
        }

        binding.eventUpdateSave.setOnClickListener {
            var isUpdated = false
            val builder = Event.Builder(event.toEvent())

            val titleText = binding.eventUpdateTitleInput.text.toString()
            if (titleText.isNotBlank() && titleText != event.title) {
                isUpdated = true
                builder.title(titleText)
            }

            val venueText = binding.eventUpdateVenueInput.text.toString()
            if (venueText.isNotBlank() && venueText != event.venue) {
                isUpdated = true
                builder.venue(venueText)
            }

            val descriptionText = binding.eventUpdateDescriptionInput.text.toString()
            if (descriptionText.isNotBlank() && descriptionText != event.description) {
                isUpdated = true
                builder.description(descriptionText)
            }

            //TODO DATE
            if (isUpdated) {
                eventService.updateEvent(event.id, builder.build(), onSuccess = {
                    context?.toast("Event updated");
                    findNavController().navigateUp()
                }, onFailure = { e ->
                    context?.toast("Cannot update event.")
                    Log.e(tag, "Cannot update event.", e)
                })
            }
        }
    }
}