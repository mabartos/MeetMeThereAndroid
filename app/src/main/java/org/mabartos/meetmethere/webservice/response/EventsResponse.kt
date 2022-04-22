package org.mabartos.meetmethere.webservice.response

data class EventsResponse(
    val data: List<EventResponse>
)

data class EventResponse(
    val id: Long,
    val title: String
)