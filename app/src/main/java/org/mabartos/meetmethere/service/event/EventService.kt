package org.mabartos.meetmethere.service.event

import org.mabartos.meetmethere.data.event.Event
import org.mabartos.meetmethere.data.event.EventResponseEnum
import org.mabartos.meetmethere.data.event.EventsListItem

interface EventService {
    fun getEvents(
        onSuccess: (List<EventsListItem>) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    fun getEvent(
        id: Long,
        onSuccess: (EventsListItem) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    fun removeEvent(
        id: Long,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    )

    fun updateEvent(
        id: Long,
        event: Event,
        onSuccess: (Long) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    fun createEvent(
        event: Event,
        onSuccess: (Long) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    fun attendance(
        id: Long,
        state: EventResponseEnum,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    )
}