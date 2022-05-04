package org.mabartos.meetmethere.webservice

import org.mabartos.meetmethere.data.event.Event
import org.mabartos.meetmethere.data.event.EventsListItem
import retrofit2.Call
import retrofit2.http.*

interface EventsApi {

    @GET("events/")
    fun getAllEvents(
        @Query("token") token: String,
    ): Call<EventsListItem>

    @GET("events/@{id}/")
    fun getSingleEvent(
        @Path("id") id: Long,
        @Query("token") token: String,
    ): Call<EventsListItem>

    @POST("events/")
    fun createEvent(event: Event): Call<Long>

    @PATCH("events/@{id}/")
    fun updateEvent(id: Long, event: Event): Call<Long>

}