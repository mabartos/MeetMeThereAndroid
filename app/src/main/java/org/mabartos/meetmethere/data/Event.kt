package org.mabartos.meetmethere.data

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
    val longitude: Double,
    val latitude: Double
) : Parcelable