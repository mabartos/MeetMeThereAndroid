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
import org.mabartos.meetmethere.data.event.EventResponseEnum
import org.mabartos.meetmethere.databinding.FragmentEventCreateBinding
import org.mabartos.meetmethere.service.event.EventService
import org.mabartos.meetmethere.service.event.EventServiceUtil
import org.mabartos.meetmethere.util.*
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

class CreateEventFragment(
    private val eventService: EventService = EventServiceUtil.createService()
) : Fragment() {

    private lateinit var binding: FragmentEventCreateBinding
    private var startDate: Calendar? = null
    private var endDate: Calendar? = null
    private var startTime: LocalTime? = null
    private var endTime: LocalTime? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventCreateBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.eventCreateToolbar.setNavigationIcon(R.drawable.ic_close)
        binding.eventCreateToolbar.setCollapseIcon(R.drawable.ic_burger_menu)
        binding.eventCreateToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.eventCreateToolbar.title = "Create event"

        binding.eventCreateStartDayInput.setOnClickListener {
            context?.datePicker(
                parentFragmentManager,
                title = R.string.start_day_settings.toString(),
                onPositiveClick = { calendar ->
                    startDate = calendar
                    binding.eventCreateStartDay.hint = context?.formatDate("dd.MM", calendar.time)
                }
            )
        }

        binding.eventCreateStartTimeInput.setOnClickListener {
            context?.timePicker(
                parentFragmentManager,
                title = "Set start time",
                onPositiveClick = { time ->
                    startTime = time
                    binding.eventCreateStartTime.hint = context?.formatTime(time)
                })
        }

        binding.eventCreateEndDayInput.setOnClickListener {
            context?.datePicker(
                parentFragmentManager,
                title = R.string.end_day_settings.toString(),
                onPositiveClick = { calendar ->
                    endDate = calendar
                    binding.eventCreateEndDay.hint = context?.formatDate("dd.MM", calendar.time)
                }
            )
        }

        binding.eventCreateEndTimeInput.setOnClickListener {
            context?.timePicker(
                parentFragmentManager,
                title = "Set end time",
                onPositiveClick = { time ->
                    endTime = time
                    binding.eventCreateEndTime.hint = context?.formatTime(time)
                })
        }

        //TODO
        binding.eventCreateSaveButton.setOnClickListener {
            val titleText = binding.eventCreateNameInput.text.toString()
            val venueText = binding.eventCreateVenueInput.text.toString()
            val descriptionText = binding.eventCreateDescriptionInput.text.toString()

            if (startDate == null || endDate == null || startTime == null || endTime == null) {
                context?.toast("You need to specify a valid time")
                return@setOnClickListener
            }

            val createEvent =
                Event(
                    title = titleText,
                    venue = venueText,
                    imageUrl = "\\TODO",
                    description = descriptionText,
                    startTime = LocalDateTime.of(
                        startDate!!.get(Calendar.YEAR),
                        startDate!!.get(Calendar.MONTH),
                        startDate!!.get(Calendar.DAY_OF_MONTH),
                        startTime!!.hour,
                        startTime!!.minute
                    ),
                    endTime = LocalDateTime.of(
                        endDate!!.get(Calendar.YEAR),
                        endDate!!.get(Calendar.MONTH),
                        endDate!!.get(Calendar.DAY_OF_MONTH),
                        endTime!!.hour,
                        endTime!!.minute
                    ),
                    isPublic = true, //TODO
                    longitude = 20.0,
                    latitude = 20.0,
                    response = EventResponseEnum.ACCEPT.textForm
                )

            eventService.createEvent(createEvent, onSuccess = {
                context?.toast("Event was created");
                findNavController().navigateUp()
            }, onFailure = { e ->
                context?.toast("Cannot create event.")
                Log.e(tag.toString(), "Cannot create event", e)
            })
        }

    }
}