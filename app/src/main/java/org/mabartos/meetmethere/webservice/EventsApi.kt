package org.mabartos.meetmethere.webservice

import org.mabartos.meetmethere.data.Event
import org.mabartos.meetmethere.data.EventsListItem
import org.mabartos.meetmethere.webservice.response.EventResponse
import org.mabartos.meetmethere.webservice.response.EventsResponse
import retrofit2.Call
import retrofit2.http.*

interface EventsApi {

    @GET("events/")
    fun getAllEvents(
        @Query("token") token: String,
    ): Call<EventsResponse>

    @GET("events/@{id}/")
    fun getSingleEvent(
        @Path("id") id: Long,
        @Query("token") token: String,
    ): Call<EventResponse>

    @POST("events/")
    fun createEvent(event: Event): Call<Long>

    @PATCH("events/@{id}/")
    fun updateEvent(id: Long, event: Event): Call<Long>

}