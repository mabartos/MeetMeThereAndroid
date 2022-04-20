package org.mabartos.meetmethere.ui.list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import org.mabartos.meetmethere.data.EventsListItem
import org.mabartos.meetmethere.databinding.EventListItemBinding

class EventsViewHolder(private val binding: EventListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    //TODO edit
    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(
        listItem: EventsListItem,
        onItemClick: (EventsListItem) -> Unit
    ) {
        binding.eventTitle.text = listItem.title
        binding.dateViewMonth.text = listItem.startTime.month.name
        binding.dateViewDay.text = listItem.startTime.dayOfMonth.toString()
        binding.eventInvitedBy.text = listItem.createdByName

        binding.eventCard.setOnClickListener {
            onItemClick(listItem)
        }
    }

}