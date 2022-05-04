package org.mabartos.meetmethere.service

import org.mabartos.meetmethere.data.Event
import org.mabartos.meetmethere.data.EventsListItem

interface EventService {
    fun getEvents(): List<EventsListItem>
    fun getEvent(id: Long): EventsListItem?
    fun removeEvent(id: Long)
    fun updateEvent(id: Long, event: Event)
    fun createEvent(event: Event): EventsListItem

    fun <T> callback(
        supplier: () -> T,
        onSuccess: (T) -> Unit,
        onFailure: (Throwable) -> Unit
    )
}