package org.mabartos.meetmethere.repository

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import org.mabartos.meetmethere.data.EventsListItem
import org.mabartos.meetmethere.webservice.EventsApi
import org.mabartos.meetmethere.webservice.RetrofitUtil
import java.time.LocalDateTime
import java.util.concurrent.ThreadLocalRandom

class EventsRepository(
    context: Context,
    private val eventsApi: EventsApi = RetrofitUtil.createAqiWebService()
) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMockedData(count: Int = 10): List<EventsListItem> =
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
                    responseType = if (it % 2 == 0) "accepted" else "declined",
                    longitude = ThreadLocalRandom.current().nextDouble(16.38, 16.58),
                    latitude = ThreadLocalRandom.current().nextDouble(48.90, 49.20)
                )
                add(item)
            }
        }


}