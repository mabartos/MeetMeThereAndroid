package org.mabartos.meetmethere.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
class EventsListItem(
    val id: Long,
    val title: String,
    val venue: String,
    val imageUrl: String,
    val description: String,
    val isPublic: Boolean,
    val createdById: Long,
    val createdByName: String,
    val updatedById: Long,
    val updatedByName: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
) : Parcelable {
}