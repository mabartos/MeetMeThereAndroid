package org.mabartos.meetmethere.data.event

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
class Event(
    val title: String,
    val venue: String,
    val imageUrl: String,
    val description: String,
    val isPublic: Boolean,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val response: String,
    val longitude: Double,
    val latitude: Double
) : Parcelable {

    class Builder(event: Event) {
        private var title: String = event.title;
        private var venue: String = event.venue;
        private var imageUrl: String = event.imageUrl;
        private var description: String = event.description;
        private var isPublic: Boolean = event.isPublic;
        private var startTime: LocalDateTime = event.startTime;
        private var endTime: LocalDateTime = event.endTime;
        private var response: String = event.response;
        private var longitude: Double = event.longitude;
        private var latitude: Double = event.latitude

        fun title(title: String) = apply { this.title = title }
        fun venue(venue: String) = apply { this.venue = venue }
        fun imageUrl(imageUrl: String) = apply { this.imageUrl = imageUrl }
        fun description(description: String) = apply { this.description = description }
        fun isPublic(isPublic: Boolean) = apply { this.isPublic = isPublic }
        fun startTime(startTime: LocalDateTime) = apply { this.startTime = startTime }
        fun endTime(endTime: LocalDateTime) = apply { this.endTime = endTime }
        fun response(response: String) = apply { this.response = response }
        fun longitude(longitude: Double) = apply { this.longitude = longitude }
        fun latitude(latitude: Double) = apply { this.latitude = latitude }

        fun build() = Event(
            title = title,
            venue = venue,
            imageUrl = imageUrl,
            description = description,
            isPublic = isPublic,
            startTime = startTime,
            endTime = endTime,
            response = response,
            longitude = longitude,
            latitude = latitude
        )
    }

}