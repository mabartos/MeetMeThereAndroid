package org.mabartos.meetmethere.ui.list

import android.content.res.Resources
import androidx.recyclerview.widget.RecyclerView
import com.nostra13.universalimageloader.core.ImageLoader
import org.mabartos.meetmethere.R
import org.mabartos.meetmethere.data.event.EventsListItem
import org.mabartos.meetmethere.databinding.EventListItemBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EventsViewHolder(
    private val binding: EventListItemBinding,
    private val resources: Resources
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        listItem: EventsListItem,
        onItemClick: (EventsListItem) -> Unit
    ) {
        binding.eventTitle.text = listItem.title
        binding.eventVenue.text = listItem.venue

        val startTime: LocalDateTime = listItem.startTime
        val endTime: LocalDateTime = listItem.endTime

        val startTimeText = startTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        val endTimeText = endTime.format(DateTimeFormatter.ofPattern("HH:mm"))

        binding.eventTime.text = String.format("%s - %s", startTimeText, endTimeText)

        binding.dateViewMonth.text = startTime.month.name.substring(0, 3)
        binding.dateViewDay.text = startTime.dayOfMonth.toString()
        binding.eventInvitedBy.text =
            "${resources.getString(R.string.invited_by)} ${listItem.createdByName}"

        if (listItem.imageUrl.isNotBlank()) {
            ImageLoader.getInstance().displayImage(listItem.imageUrl, binding.eventImage)
        } else {
            binding.eventImage.background = resources.getDrawable(R.drawable.no_image)
        }

        binding.eventCard.setOnClickListener {
            onItemClick(listItem)
        }
    }

}
