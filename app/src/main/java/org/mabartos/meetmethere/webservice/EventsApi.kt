package org.mabartos.meetmethere.webservice

import org.mabartos.meetmethere.webservice.response.EventResponse
import org.mabartos.meetmethere.webservice.response.EventsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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

}