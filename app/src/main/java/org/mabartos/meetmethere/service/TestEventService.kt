package org.mabartos.meetmethere.service

import android.os.Build
import androidx.annotation.RequiresApi
import org.mabartos.meetmethere.data.Event
import org.mabartos.meetmethere.data.EventsListItem
import java.time.LocalDateTime
import java.util.concurrent.ThreadLocalRandom

@RequiresApi(Build.VERSION_CODES.O)
class TestEventService : EventService {

    private val events: MutableList<EventsListItem> = getMockedData(10)

    private fun getMockedData(count: Int = 10): MutableList<EventsListItem> =
        mutableListOf<EventsListItem>().apply {
            repeat(count) {
                val item = EventsListItem(
                    id = it.toLong(),
                    title = "Event $it",
                    description = "Description for event $it",
                    venue = "Amsterdam",
                    imageUrl = "https://thumbs.dreamstime.com/z/bottles-famous-global-beer-brands-poznan-pol-mar-including-heineken-becks-bud-miller-corona-stella-artois-san-miguel-143170440.jpg",
                    isPublic = it % 2 == 0,
                    createdByName = "User$it Doe",
                    createdById = it.toLong(),
                    updatedByName = "User${it + 1} Updater",
                    updatedById = 1 + it.toLong(),
                    startTime = LocalDateTime.of(2020, 8, 22, 2 + it, 0, 0),
                    endTime = LocalDateTime.of(2020, 8, 22, 3 + it, 0, 0),
                    response = if (it % 2 == 0) "accepted" else "declined",
                    longitude = ThreadLocalRandom.current().nextDouble(16.38, 16.58),
                    latitude = ThreadLocalRandom.current().nextDouble(48.90, 49.20)
                )
                add(item)
            }
        }

    override fun getEvents(): List<EventsListItem> {
        return events
    }

    override fun getEvent(id: Long): EventsListItem? {
        if (isInvalidIndex(id)) return null
        return events.find { event -> event.id == id }
    }

    override fun removeEvent(id: Long) {
        if (isInvalidIndex(id)) return
        events.removeAt(id.toInt())
    }

    override fun updateEvent(id: Long, event: Event) {
        if (isInvalidIndex(id)) return
        events[id.toInt()] = mapEventToEventItem(id, event)
    }

    override fun createEvent(event: Event): EventsListItem {
        val eventItem = mapEventToEventItem(event.hashCode().toLong(), event)
        events.add(eventItem)
        return eventItem
    }

    override fun <T> callback(
        supplier: () -> T,
        onSuccess: (T) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        try {
            onSuccess(supplier.invoke())
        } catch (e: Exception) {
            onFailure(e)
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

}