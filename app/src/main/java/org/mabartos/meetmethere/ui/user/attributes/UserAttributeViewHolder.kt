package org.mabartos.meetmethere.ui.user.attributes

import androidx.recyclerview.widget.RecyclerView
import org.mabartos.meetmethere.databinding.UserAttributeItemBinding

class UserAttributeViewHolder(private val binding: UserAttributeItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        item: UserAttributeItem,
        onItemClick: (UserAttributeItem) -> Unit,
        onDeleteClick: (UserAttributeItem) -> Unit
    ) {
        binding.userAttributeKey.text = item.key
        binding.userAttributeValue.text = item.value

        binding.userAttributeCard.setOnClickListener {
            onItemClick(item)
        }

        binding.userAttributeDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }
}