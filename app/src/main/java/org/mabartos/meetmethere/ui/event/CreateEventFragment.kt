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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class CreateEventFragment(
    private val eventService: EventService = EventServiceUtil.createService()
) : Fragment() {

    private lateinit var binding: FragmentEventCreateBinding
    private var startDate: LocalDate? = null
    private var endDate: LocalDate? = null
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
        binding.eventCreateToolbar.title = resources.getString(R.string.create_event)

        binding.eventCreateStartDayInput.setOnClickListener {
            context?.datePicker(
                parentFragmentManager,
                title = resources.getString(R.string.start_day_settings),
                onPositiveClick = { date ->
                    startDate = date
                    binding.eventCreateStartDay.hint = context?.formatDate("dd.MM", date)
                }
            )
        }

        binding.eventCreateStartTimeInput.setOnClickListener {
            context?.timePicker(
                parentFragmentManager,
                title = resources.getString(R.string.start_time_settings),
                onPositiveClick = { time ->
                    startTime = time
                    binding.eventCreateStartTime.hint = context?.formatTime(time)
                })
        }

        binding.eventCreateEndDayInput.setOnClickListener {
            context?.datePicker(
                parentFragmentManager,
                title = resources.getString(R.string.end_day_settings),
                onPositiveClick = { date ->
                    endDate = date
                    binding.eventCreateEndDay.hint = context?.formatDate("dd.MM", date)
                }
            )
        }

        binding.eventCreateEndTimeInput.setOnClickListener {
            context?.timePicker(
                parentFragmentManager,
                title = resources.getString(R.string.end_time_settings),
                onPositiveClick = { time ->
                    endTime = time
                    binding.eventCreateEndTime.hint = context?.formatTime(time)
                })
        }

        binding.eventCreateSaveButton.setOnClickListener {
            val titleText = binding.eventCreateNameInput.text.toString()
            val venueText = binding.eventCreateVenueInput.text.toString()
            val descriptionText = binding.eventCreateDescriptionInput.text.toString()

            if (startDate == null || endDate == null || startTime == null || endTime == null) {
                context?.toast(resources.getString(R.string.specify_valid_time))
                return@setOnClickListener
            }

            val createEvent =
                Event(
                    title = titleText,
                    venue = venueText,
                    imageUrl = "",//TODO
                    description = descriptionText,
                    startTime = LocalDateTime.of(
                        startDate!!.year,
                        startDate!!.month,
                        startDate!!.dayOfMonth,
                        startTime!!.hour,
                        startTime!!.minute
                    ),
                    endTime = LocalDateTime.of(
                        endDate!!.year,
                        endDate!!.month,
                        endDate!!.dayOfMonth,
                        endTime!!.hour,
                        endTime!!.minute
                    ),
                    isPublic = true, //TODO
                    longitude = null,
                    latitude = null,
                    response = EventResponseEnum.ACCEPT.textForm
                )

            eventService.createEvent(createEvent, onSuccess = {
                context?.toast(resources.getString(R.string.event_created));
                findNavController().navigateUp()
            }, onFailure = { e ->
                context?.toast(resources.getString(R.string.event_not_created))
                Log.e(tag.toString(), "Cannot create event", e)
            })
        }

    }
}