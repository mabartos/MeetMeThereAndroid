package org.mabartos.meetmethere.service.event

import org.mabartos.meetmethere.data.event.Event
import org.mabartos.meetmethere.data.event.EventResponseEnum
import org.mabartos.meetmethere.data.event.EventsListItem
import org.mabartos.meetmethere.service.ModelNotFoundException
import org.mabartos.meetmethere.service.ServiceUtil
import java.time.LocalDateTime
import java.util.concurrent.ThreadLocalRandom

class TestEventService : EventService {

    private val events: MutableList<EventsListItem> = getMockedData(10)

    private fun getMockedData(count: Int = 10): MutableList<EventsListItem> =
        mutableListOf<EventsListItem>().apply {
            repeat(count) {
                val item = EventsListItem(
                    id = it.toLong(),
                    title = "Event $it",
                    description = "Description for event $it. This event is on a special venue and you need to bring your own instruments there. We have wine, beer and some kind of drugs.",
                    venue = "Amsterdam",
                    imageUrl = if (it % 4 > 0) "https://thumbs.dreamstime.com/z/bottles-famous-global-beer-brands-poznan-pol-mar-including-heineken-becks-bud-miller-corona-stella-artois-san-miguel-143170440.jpg" else "",
                    isPublic = it % 2 == 0,
                    createdByName = "User$it Doe",
                    createdById = it.toLong(),
                    updatedByName = "User${it + 1} Updater",
                    updatedById = 1 + it.toLong(),
                    startTime = LocalDateTime.of(2020, 8, 22 + it, 2 + it, 0, 0),
                    endTime = LocalDateTime.of(2020, 8, 22 + it, 3 + it, 0, 0),
                    response = EventResponseEnum.NOT_ANSWERED.textForm,
                    longitude = ThreadLocalRandom.current().nextDouble(16.53, 16.58),
                    latitude = ThreadLocalRandom.current().nextDouble(49.15, 49.20)
                )
                add(item)
            }
        }

    private fun isInvalidIndex(id: Long): Boolean {
        return id < 0 || id >= events.size
    }

    private fun mapEventToEventItem(id: Long, event: Event): EventsListItem {
        return EventsListItem(
            id = id,
            title = event.title,
            venue = event.venue,
            imageUrl = event.imageUrl,
            description = event.description,
            isPublic = event.isPublic,
            createdById = id,
            createdByName = "UserEventId${id}",
            updatedById = id,
            updatedByName = "UserEventId${id}",
            startTime = event.startTime,
            endTime = event.endTime,
            response = event.response,
            latitude = event.latitude,
            longitude = event.longitude
        )
    }

    override fun getEvents(
        onSuccess: (List<EventsListItem>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        ServiceUtil.callback(
            supplier = {
                events
            }, onSuccess = { list ->
                onSuccess.invoke(list)
            }, onFailure = { e ->
                onFailure.invoke(e)
            })
    }

    override fun getEvent(
        id: Long,
        onSuccess: (EventsListItem) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        ServiceUtil.callback(
            supplier = {
                if (isInvalidIndex(id)) throw ModelNotFoundException()
                events.find { event -> event.id == id } ?: throw ModelNotFoundException()
            }, onSuccess = { list ->
                onSuccess.invoke(list)
            }, onFailure = { e ->
                onFailure.invoke(e)
            })
    }

    override fun removeEvent(id: Long, onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
        ServiceUtil.callback(
            supplier = {
                if (!isInvalidIndex(id)) events.removeAt(id.toInt())
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
        ServiceUtil.callback(
            supplier = {
                val found = events.find { event -> event.id == id }
                if (found != null) {
                    val index = events.indexOf(found)
                    if (index != -1) {
                        events[index] = mapEventToEventItem(id, Event.Builder(event).build())
                        return@callback id
                    }
                }
                -1
            }, onSuccess = { list ->
                onSuccess.invoke(list)
            }, onFailure = { e ->
                onFailure.invoke(e)
            })
    }

    override fun createEvent(
        event: Event,
        onSuccess: (Long) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        ServiceUtil.callback(
            supplier = {
                val eventItem = mapEventToEventItem(event.hashCode().toLong(), event)
                events.add(eventItem)
                eventItem.id
            }, onSuccess = { list ->
                onSuccess.invoke(list)
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
        ServiceUtil.callback(
            supplier = {
                val event =
                    events.find { event -> event.id == id } ?: throw ModelNotFoundException()

                if (event.response != state.textForm) {
                    val index = events.indexOf(event)
                    if (index != -1) {
                        events[index] =
                            mapEventToEventItem(id, Event.Builder(event.toEvent()).build())
                        onSuccess.invoke()
                    }
                }
            }, onSuccess = {
                onSuccess.invoke()
            }, onFailure = { e ->
                onFailure.invoke(e)
            })
    }

}