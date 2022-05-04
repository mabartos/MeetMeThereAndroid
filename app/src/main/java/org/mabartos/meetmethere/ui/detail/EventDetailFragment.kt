package org.mabartos.meetmethere.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nostra13.universalimageloader.core.ImageLoader
import org.mabartos.meetmethere.R
import org.mabartos.meetmethere.data.event.EventResponseEnum
import org.mabartos.meetmethere.data.event.EventsListItem
import org.mabartos.meetmethere.databinding.FragmentEventDetailBinding
import org.mabartos.meetmethere.service.event.EventService
import org.mabartos.meetmethere.service.event.EventServiceUtil
import org.mabartos.meetmethere.util.formatDate
import org.mabartos.meetmethere.util.formatTime
import org.mabartos.meetmethere.util.toast
import java.time.LocalDateTime

class EventDetailFragment(
    private val eventService: EventService = EventServiceUtil.createService()
) : Fragment() {

    private lateinit var binding: FragmentEventDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventDetailBinding.inflate(LayoutInflater.from(context), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.eventDetailToolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.eventDetailToolbar.setCollapseIcon(R.drawable.ic_burger_menu)
        binding.eventDetailToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val id = EventDetailFragmentArgs.fromBundle(requireArguments()).id
        val item: EventsListItem? = eventService.getEvent(id)

        if (item == null) {
            context?.toast("Event doesn't exist")
            return
        }

        binding.eventDetailToolbar.title = item.title
        binding.eventDetailTitle.text = item.title
        binding.eventDetailVenue.text = item.venue

        ImageLoader.getInstance().displayImage(item.imageUrl, binding.eventDetailImage)

        val startTime: LocalDateTime = item.startTime
        val endTime: LocalDateTime = item.endTime

        binding.eventDetailStartTitle.text = resources.getText(R.string.start)
        binding.eventDetailStartDate.text = context?.formatDate("dd.MM.yyyy", startTime)
        binding.eventDetailStartTime.text = context?.formatTime(startTime.toLocalTime())

        binding.eventDetailEndTitle.text = resources.getText(R.string.end)
        binding.eventDetailEndDate.text = context?.formatDate("dd.MM.yyyy", endTime)
        binding.eventDetailEndTime.text = context?.formatTime(endTime.toLocalTime())

        binding.eventDetailDescription.text = item.description

        binding.eventDetailEdit.setOnClickListener {
            findNavController().navigate(
                EventDetailFragmentDirections.actionDetailToUpdateEvent(
                    item
                )
            )
        }

        binding.eventDetailDelete.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.deleteThisEventQuestion)
                .setMessage(R.string.deleteEventConfirmationMsg)
                .setNeutralButton(resources.getString(R.string.cancel)) { _, _ ->
                }
                .setPositiveButton(
                    resources.getString(R.string.confirm).uppercase()
                ) { _, _ ->
                    findNavController().navigate(EventDetailFragmentDirections.actionDetailToListFragment())
                    eventService.removeEvent(item.id)
                    context?.toast("Event was deleted")
                }
                .show()
        }

        when (EventResponseEnum.getByTextForm(item.response)) {
            EventResponseEnum.ACCEPT -> {
                changeAttendanceButtonsColors(binding.eventDetailAcceptButton)
            }
            EventResponseEnum.MAYBE -> {
                changeAttendanceButtonsColors(binding.eventDetailMaybeButton)
            }
            EventResponseEnum.DECLINE -> {
                changeAttendanceButtonsColors(binding.eventDetailDeclineButton)
            }
            else -> {}
        }

        binding.eventDetailAcceptButton.setOnClickListener {
            changeAttendanceButtonsColors(binding.eventDetailAcceptButton)
            eventService.attendance(item.id, EventResponseEnum.ACCEPT)
        }

        binding.eventDetailMaybeButton.setOnClickListener {
            changeAttendanceButtonsColors(binding.eventDetailMaybeButton)
            eventService.attendance(item.id, EventResponseEnum.MAYBE)
        }

        binding.eventDetailDeclineButton.setOnClickListener {
            changeAttendanceButtonsColors(binding.eventDetailDeclineButton)
            eventService.attendance(item.id, EventResponseEnum.DECLINE)
        }
    }

    private fun changeAttendanceButtonsColors(button: Button) {
        when (button) {
            binding.eventDetailAcceptButton -> {
                binding.eventDetailAcceptButton.backgroundTintList =
                    context?.getColorStateList(R.color.green)
                binding.eventDetailAcceptButton.setTextColor(context?.getColorStateList(R.color.white))

                binding.eventDetailMaybeButton.backgroundTintList =
                    context?.getColorStateList(R.color.colorOnPrimary)
                binding.eventDetailMaybeButton.setTextColor(context?.getColorStateList(R.color.colorTextOnPrimary))
                binding.eventDetailDeclineButton.backgroundTintList =
                    context?.getColorStateList(R.color.colorOnPrimary)
                binding.eventDetailDeclineButton.setTextColor(context?.getColorStateList(R.color.colorTextOnPrimary))

            }
            binding.eventDetailMaybeButton -> {
                binding.eventDetailMaybeButton.backgroundTintList =
                    context?.getColorStateList(R.color.freesia)
                binding.eventDetailMaybeButton.setTextColor(context?.getColorStateList(R.color.white))

                binding.eventDetailAcceptButton.backgroundTintList =
                    context?.getColorStateList(R.color.colorOnPrimary)
                binding.eventDetailAcceptButton.setTextColor(context?.getColorStateList(R.color.colorTextOnPrimary))
                binding.eventDetailDeclineButton.backgroundTintList =
                    context?.getColorStateList(R.color.colorOnPrimary)
                binding.eventDetailDeclineButton.setTextColor(context?.getColorStateList(R.color.colorTextOnPrimary))

            }
            binding.eventDetailDeclineButton -> {
                binding.eventDetailDeclineButton.backgroundTintList =
                    context?.getColorStateList(R.color.red)
                binding.eventDetailDeclineButton.setTextColor(context?.getColorStateList(R.color.white))

                binding.eventDetailAcceptButton.backgroundTintList =
                    context?.getColorStateList(R.color.colorOnPrimary)
                binding.eventDetailAcceptButton.setTextColor(context?.getColorStateList(R.color.colorTextOnPrimary))
                binding.eventDetailMaybeButton.backgroundTintList =
                    context?.getColorStateList(R.color.colorOnPrimary)
                binding.eventDetailMaybeButton.setTextColor(context?.getColorStateList(R.color.colorTextOnPrimary))
            }
        }
    }
}