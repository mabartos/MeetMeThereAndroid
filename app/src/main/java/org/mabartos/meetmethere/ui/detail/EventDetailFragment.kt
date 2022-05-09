package org.mabartos.meetmethere.ui.detail

import android.os.Bundle
import android.util.Log
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
    private var currentResponse: EventResponseEnum? = EventResponseEnum.NOT_ANSWERED

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

        val event = EventDetailFragmentArgs.fromBundle(requireArguments()).event

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
                    eventService.removeEvent(event.id, onSuccess = {
                        context?.toast("Event was deleted")
                    }, onFailure = { e ->
                        context?.toast("Cannot delete event")
                        Log.e(
                            EventDetailFragment::getTag.toString(),
                            "Cannot delete event", e
                        )
                    })
                }
                .show()
        }

        binding.eventDetailAcceptButton.setOnClickListener {
            changeAttendanceButtonsColors(binding.eventDetailAcceptButton)
            currentResponse = checkCurrentResponse(event.id, EventResponseEnum.ACCEPT)
        }

        binding.eventDetailMaybeButton.setOnClickListener {
            changeAttendanceButtonsColors(binding.eventDetailMaybeButton)
            currentResponse = checkCurrentResponse(event.id, EventResponseEnum.MAYBE)
        }

        binding.eventDetailDeclineButton.setOnClickListener {
            changeAttendanceButtonsColors(binding.eventDetailDeclineButton)
            currentResponse = checkCurrentResponse(event.id, EventResponseEnum.DECLINE)
        }

        initData(event)

        eventService.getEvent(event.id,
            onSuccess = { item ->
                initData(item)
            }, onFailure = { e ->
                context?.toast("Event doesn't exist")
                Log.e(tag.toString(), e.message ?: e.toString())
            })
    }

    private fun checkCurrentResponse(
        itemId: Long,
        checkedEnum: EventResponseEnum
    ): EventResponseEnum {
        return if (currentResponse == checkedEnum) {
            clearAttendanceButtons()
            eventService.attendance(
                itemId,
                EventResponseEnum.NOT_ANSWERED,
                onSuccess = {},
                onFailure = {})
            EventResponseEnum.NOT_ANSWERED
        } else {
            eventService.attendance(itemId, checkedEnum, onSuccess = {}, onFailure = {})
            checkedEnum
        }
    }

    private fun clearAttendanceButtons() {
        binding.eventDetailAcceptButton.backgroundTintList =
            context?.getColorStateList(R.color.colorOnPrimary)
        binding.eventDetailAcceptButton.setTextColor(context?.getColorStateList(R.color.colorTextOnPrimary))
        binding.eventDetailMaybeButton.backgroundTintList =
            context?.getColorStateList(R.color.colorOnPrimary)
        binding.eventDetailMaybeButton.setTextColor(context?.getColorStateList(R.color.colorTextOnPrimary))
        binding.eventDetailDeclineButton.backgroundTintList =
            context?.getColorStateList(R.color.colorOnPrimary)
        binding.eventDetailDeclineButton.setTextColor(context?.getColorStateList(R.color.colorTextOnPrimary))
    }

    // TODO Refactor
    private fun changeAttendanceButtonsColors(button: Button?) {
        if (button == null) {
            clearAttendanceButtons()
            return
        }

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

    private fun initData(item: EventsListItem) {

        binding.eventDetailToolbar.title = item.title
        binding.eventDetailTitle.text = item.title
        binding.eventDetailVenue.text = item.venue

        binding.eventDetailEdit.setOnClickListener {
            findNavController().navigate(
                EventDetailFragmentDirections.actionDetailToUpdateEvent(
                    item
                )
            )
        }

        if (item.imageUrl.isNotBlank()) {
            ImageLoader.getInstance().displayImage(item.imageUrl, binding.eventDetailImage)
        } else {
            binding.eventDetailImage.visibility = View.GONE
        }

        val startTime: LocalDateTime = item.startTime
        val endTime: LocalDateTime = item.endTime

        binding.eventDetailStartTitle.text = resources.getText(R.string.start)
        binding.eventDetailStartDate.text = context?.formatDate("dd.MM.yyyy", startTime)
        binding.eventDetailStartTime.text = context?.formatTime(startTime.toLocalTime())

        binding.eventDetailEndTitle.text = resources.getText(R.string.end)
        binding.eventDetailEndDate.text = context?.formatDate("dd.MM.yyyy", endTime)
        binding.eventDetailEndTime.text = context?.formatTime(endTime.toLocalTime())

        binding.eventDetailDescription.text = item.description

        val response = EventResponseEnum.getByTextForm(item.response)
        this.currentResponse = response

        when (response) {
            EventResponseEnum.ACCEPT -> {
                changeAttendanceButtonsColors(binding.eventDetailAcceptButton)
            }
            EventResponseEnum.MAYBE -> {
                changeAttendanceButtonsColors(binding.eventDetailMaybeButton)
            }
            EventResponseEnum.DECLINE -> {
                changeAttendanceButtonsColors(binding.eventDetailDeclineButton)
            }
            EventResponseEnum.NOT_ANSWERED -> {
                clearAttendanceButtons()
            }
            else -> {
                context?.toast("Unknown response type!")
            }
        }
    }
}