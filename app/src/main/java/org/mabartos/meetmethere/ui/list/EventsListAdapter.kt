package org.mabartos.meetmethere.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.mabartos.meetmethere.data.event.EventsListItem
import org.mabartos.meetmethere.databinding.EventListItemBinding

class EventsListAdapter(
    private val onItemClick: (EventsListItem) -> Unit,
) : RecyclerView.Adapter<EventsViewHolder>() {

    private var listItems: MutableList<EventsListItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        val binding = EventListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        holder.bind(listItems[position], onItemClick)
    }

    fun submitList(newListItems: List<EventsListItem>) {
        listItems = newListItems.toMutableList()
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        listItems.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int = listItems.size
}