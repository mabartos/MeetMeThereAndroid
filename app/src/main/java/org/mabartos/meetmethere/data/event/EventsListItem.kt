package org.mabartos.meetmethere.data.event

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
    val endTime: LocalDateTime,
    val response: String,
    val longitude: Double,
    val latitude: Double
) : Parcelable {

    fun toEvent(): Event {
        return toEvent(this)
    }

    companion object {
        fun toEvent(item: EventsListItem): Event {
            return Event(
                title = item.title,
                venue = item.venue,
                imageUrl = item.imageUrl,
                description = item.description,
                isPublic = item.isPublic,
                startTime = item.startTime,
                endTime = item.endTime,
                response = item.response,
                longitude = item.longitude,
                latitude = item.latitude
            )
        }
    }
}