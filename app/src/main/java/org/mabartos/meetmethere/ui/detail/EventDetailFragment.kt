package org.mabartos.meetmethere.ui.detail

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nostra13.universalimageloader.core.ImageLoader
import org.mabartos.meetmethere.R
import org.mabartos.meetmethere.data.EventsListItem
import org.mabartos.meetmethere.databinding.FragmentEventDetailBinding
import org.mabartos.meetmethere.repository.EventsRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class EventDetailFragment : Fragment() {

    private lateinit var binding: FragmentEventDetailBinding

    private val eventsRepository: EventsRepository by lazy {
        EventsRepository(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventDetailBinding.inflate(LayoutInflater.from(context), container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.eventDetailToolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.eventDetailToolbar.setCollapseIcon(R.drawable.ic_burger_menu)
        binding.eventDetailToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val id = EventDetailFragmentArgs.fromBundle(requireArguments()).id

        // TODO fetch data
        val item: EventsListItem = eventsRepository.getMockedData()
            .stream()
            .filter { it.id == id }
            .findFirst()
            .orElse(null)

        binding.eventDetailToolbar.title = item.title
        binding.eventDetailTitle.text = item.title
        binding.eventDetailVenue.text = item.venue

        ImageLoader.getInstance().displayImage(item.imageUrl, binding.eventDetailImage)

        val startTime: LocalDateTime = item.startTime
        val endTime: LocalDateTime = item.endTime

        val startTimeText = startTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        val endTimeText = endTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))

        binding.eventDetailTime.text = String.format("%s - %s", startTimeText, endTimeText)
        binding.eventDetailDescription.text = item.description

        binding.eventDetailEdit.setOnClickListener {
            findNavController().navigate(
                EventDetailFragmentDirections.actionDetailToUpdateEvent(
                    item
                )
            )
        }

        binding.eventDetailDelete.setOnClickListener {
            val title = resources.getString(R.string.delete) + " '${item.title}' ?"

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setMessage(R.string.deleteEventConfirmationMsg)
                .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                }
                .setPositiveButton(
                    resources.getString(R.string.confirm).uppercase()
                ) { dialog, which ->
                    findNavController().navigate(EventDetailFragmentDirections.actionDetailToListFragment())
                    //TODO delete
                }
                .show()
        }

        when (item.responseType) {
            "accept" -> {
                changeAttendanceButtonsColors(binding.eventDetailAcceptButton)
            }
            "maybe" -> {
                changeAttendanceButtonsColors(binding.eventDetailMaybeButton)
            }
            "decline" -> {
                changeAttendanceButtonsColors(binding.eventDetailDeclineButton)
            }
        }

        binding.eventDetailAcceptButton.setOnClickListener {
            changeAttendanceButtonsColors(binding.eventDetailAcceptButton)
            //TODO
        }

        binding.eventDetailMaybeButton.setOnClickListener {
            changeAttendanceButtonsColors(binding.eventDetailMaybeButton)
            //TODO
        }

        binding.eventDetailDeclineButton.setOnClickListener {
            changeAttendanceButtonsColors(binding.eventDetailDeclineButton)
            //TODO
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun changeAttendanceButtonsColors(button: Button) {
        if (button == binding.eventDetailAcceptButton) {
            binding.eventDetailAcceptButton.backgroundTintList =
                context?.getColorStateList(R.color.green)
            binding.eventDetailAcceptButton.setTextColor(context?.getColorStateList(R.color.white))

            binding.eventDetailMaybeButton.backgroundTintList =
                context?.getColorStateList(R.color.colorOnPrimary)
            binding.eventDetailMaybeButton.setTextColor(context?.getColorStateList(R.color.colorTextOnPrimary))
            binding.eventDetailDeclineButton.backgroundTintList =
                context?.getColorStateList(R.color.colorOnPrimary)
            binding.eventDetailDeclineButton.setTextColor(context?.getColorStateList(R.color.colorTextOnPrimary))

        } else if (button == binding.eventDetailMaybeButton) {
            binding.eventDetailMaybeButton.backgroundTintList =
                context?.getColorStateList(R.color.freesia)
            binding.eventDetailMaybeButton.setTextColor(context?.getColorStateList(R.color.white))

            binding.eventDetailAcceptButton.backgroundTintList =
                context?.getColorStateList(R.color.colorOnPrimary)
            binding.eventDetailAcceptButton.setTextColor(context?.getColorStateList(R.color.colorTextOnPrimary))
            binding.eventDetailDeclineButton.backgroundTintList =
                context?.getColorStateList(R.color.colorOnPrimary)
            binding.eventDetailDeclineButton.setTextColor(context?.getColorStateList(R.color.colorTextOnPrimary))

        } else if (button == binding.eventDetailDeclineButton) {
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