package org.mabartos.meetmethere.webservice

import org.mabartos.meetmethere.data.event.Event
import org.mabartos.meetmethere.data.event.EventsListItem
import retrofit2.Call
import retrofit2.http.*

interface EventsApi {

    @GET("events/")
    fun getAllEvents(
        @Header("Authorization") token: String,
    ): Call<List<EventsListItem>>

    @GET("events/@{id}/")
    fun getSingleEvent(
        @Path("id") id: Long,
        @Header("Authorization") token: String,
    ): Call<EventsListItem>

    @DELETE("events/@{id}")
    fun removeEvent(id: Long, @Header("Authorization") token: String): Call<Unit>

    @POST("events/")
    fun createEvent(event: Event, @Header("Authorization") token: String): Call<Long>

    @PATCH("events/@{id}/")
    fun updateEvent(
        @Path("id") id: Long,
        event: Event,
        @Header("Authorization") token: String
    ): Call<Long>

    @POST("events/@{id}/response")
    fun attendance(@Path("id") id: Long, @Header("Authorization") token: String): Call<Unit>

}