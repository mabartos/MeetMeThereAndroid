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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class UpdateEventFragment(
    private val eventService: EventService = EventServiceUtil.createService()
) : Fragment() {

    private lateinit var binding: FragmentEventUpdateBinding

    private var startDate: LocalDate? = null
    private lateinit var startDateOriginal: LocalDate

    private var endDate: LocalDate? = null
    private lateinit var endDateOriginal: LocalDate

    private var startTime: LocalTime? = null
    private lateinit var startTimeOriginal: LocalTime

    private var endTime: LocalTime? = null
    private lateinit var endTimeOriginal: LocalTime

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
        binding.eventUpdateToolbar.title = resources.getString(R.string.event_updated)

        val event = UpdateEventFragmentArgs.fromBundle(requireArguments()).event

        binding.eventUpdateTitleInput.setText(event.title)
        binding.eventUpdateVenueInput.setText(event.venue)
        binding.eventUpdateDescriptionInput.setText(event.description)

        startDateOriginal = event.startTime.toLocalDate()
        startTimeOriginal = event.startTime.toLocalTime()

        endDateOriginal = event.endTime.toLocalDate()
        endTimeOriginal = event.endTime.toLocalTime()

        binding.eventUpdateStartDay.hint = context?.formatDate("dd.MM", event.startTime)
        binding.eventUpdateStartTime.hint = context?.formatTime(event.startTime.toLocalTime())
        binding.eventUpdateEndDay.hint = context?.formatDate("dd.MM", event.endTime)
        binding.eventUpdateEndTime.hint = context?.formatTime(event.endTime.toLocalTime())

        binding.eventUpdateStartDayInput.setOnClickListener {
            context?.datePicker(
                parentFragmentManager,
                title = resources.getString(R.string.start_day_settings),
                onPositiveClick = { date ->
                    startDate = date
                    binding.eventUpdateStartDay.hint =
                        context?.formatDate("dd.MM", date)
                }
            )
        }

        binding.eventUpdateStartTimeInput.setOnClickListener {
            context?.timePicker(
                parentFragmentManager,
                title = resources.getString(R.string.start_time_settings),
                onPositiveClick = { time ->
                    startTime = time
                    binding.eventUpdateStartTime.hint = context?.formatTime(time)
                })
        }

        binding.eventUpdateEndDayInput.setOnClickListener {
            context?.datePicker(
                parentFragmentManager,
                title = resources.getString(R.string.end_day_settings),
                onPositiveClick = { date ->
                    endDate = date
                    binding.eventUpdateEndDay.hint =
                        context?.formatDate("dd.MM", date)
                }
            )
        }

        binding.eventUpdateEndTimeInput.setOnClickListener {
            context?.timePicker(
                parentFragmentManager,
                title = resources.getString(R.string.end_time_settings),
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

            if (startDate != null && startDate != startDateOriginal) {
                isUpdated = true
                val finalDate = LocalDateTime.of(startDate, startTime ?: startTimeOriginal)
                builder.startTime(finalDate)
            }

            if (startTime != null && startTime != startTimeOriginal) {
                isUpdated = true
                val finalDate = LocalDateTime.of(startDate ?: startDateOriginal, startTime)
                builder.startTime(finalDate)
            }

            if (endDate != null && endDate != endDateOriginal) {
                isUpdated = true
                val finalDate = LocalDateTime.of(endDate, endTime ?: endTimeOriginal)
                builder.endTime(finalDate)
            }

            if (endTime != null && endTime != endTimeOriginal) {
                isUpdated = true
                val finalDate = LocalDateTime.of(endDate ?: endDateOriginal, endTime)
                builder.endTime(finalDate)
            }

            if (isUpdated) {
                eventService.updateEvent(event.id, builder.build(), onSuccess = {
                    context?.toast(resources.getString(R.string.event_updated));
                    findNavController().navigateUp()
                }, onFailure = { e ->
                    context?.toast(resources.getString(R.string.event_not_updated))
                    Log.e(tag, "Cannot update event.", e)
                })
            }
        }
    }
}