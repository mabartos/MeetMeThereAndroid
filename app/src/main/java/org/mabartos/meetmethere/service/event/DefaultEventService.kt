package org.mabartos.meetmethere.service.event

import org.mabartos.meetmethere.data.event.Event
import org.mabartos.meetmethere.data.event.EventResponseEnum
import org.mabartos.meetmethere.data.event.EventsListItem
import org.mabartos.meetmethere.webservice.EventsApi
import org.mabartos.meetmethere.webservice.RetrofitUtil

class DefaultEventService(
    private val eventsApi: EventsApi = RetrofitUtil.createAqiWebService(),
) : EventService {

    private var token: String = ""

    override fun getEvents(
        onSuccess: (List<EventsListItem>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        RetrofitUtil.callback(supplier = {
            eventsApi.getAllEvents(token)
        }, onSuccess = { list ->
            onSuccess.invoke(list)
        }, onFailure = { e ->
            onFailure.invoke(e)
        })
    }

    override fun getEvent(
        id: Long,
        onSuccess: (EventsListItem?) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        RetrofitUtil.callback(supplier = {
            eventsApi.getSingleEvent(id, token)
        }, onSuccess = { list ->
            onSuccess.invoke(list)
        }, onFailure = { e ->
            onFailure.invoke(e)
        })
    }

    override fun removeEvent(id: Long, onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
        RetrofitUtil.callback(supplier = {
            eventsApi.removeEvent(id, token)
        }, onSuccess = {
            onSuccess.invoke()
        }, onFailure = { e ->
            onFailure.invoke(e)
        })
    }

    override fun updateEvent(
        id: Long,
        event: Event,
        onSuccess: (Long) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        RetrofitUtil.callback(supplier = {
            eventsApi.updateEvent(id, event, token)
        }, onSuccess = { id ->
            onSuccess.invoke(id)
        }, onFailure = { e ->
            onFailure.invoke(e)
        })
    }

    override fun createEvent(
        event: Event,
        onSuccess: (Long) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        RetrofitUtil.callback(supplier = {
            eventsApi.createEvent(event, token)
        }, onSuccess = { id ->
            onSuccess.invoke(id)
        }, onFailure = { e ->
            onFailure.invoke(e)
        })
    }

    override fun attendance(
        id: Long,
        state: EventResponseEnum,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        RetrofitUtil.callback(supplier = {
            eventsApi.attendance(id, token)
        }, onSuccess = {
            onSuccess.invoke()
        }, onFailure = { e ->
            onFailure.invoke(e)
        })
    }
}