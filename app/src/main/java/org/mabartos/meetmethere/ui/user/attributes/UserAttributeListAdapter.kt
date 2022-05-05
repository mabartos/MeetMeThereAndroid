package org.mabartos.meetmethere.ui.user.attributes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.mabartos.meetmethere.databinding.UserAttributeItemBinding

class UserAttributeListAdapter(
    private val onItemClick: (UserAttributeItem) -> Unit,
    private val onDeleteClick: (UserAttributeItem) -> Unit
) :
    RecyclerView.Adapter<UserAttributeViewHolder>() {

    private var attributes: MutableList<UserAttributeItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAttributeViewHolder {
        val binding =
            UserAttributeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserAttributeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserAttributeViewHolder, position: Int) {
        holder.bind(attributes[position], onItemClick, onDeleteClick)
    }

    fun submitList(newListItems: List<UserAttributeItem>) {
        attributes = newListItems.toMutableList()
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        attributes.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int = attributes.size
}