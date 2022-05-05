package org.mabartos.meetmethere.service.event

import org.mabartos.meetmethere.data.event.Event
import org.mabartos.meetmethere.data.event.EventResponseEnum
import org.mabartos.meetmethere.data.event.EventsListItem

interface EventService {
    fun getEvents(): List<EventsListItem>
    fun getEvent(id: Long): EventsListItem?
    fun removeEvent(id: Long)
    fun updateEvent(id: Long, event: Event)
    fun createEvent(event: Event): EventsListItem

    fun attendance(id: Long, state: EventResponseEnum)
}