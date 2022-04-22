package org.mabartos.meetmethere.repository

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import org.mabartos.meetmethere.data.EventsListItem
import org.mabartos.meetmethere.webservice.EventsApi
import org.mabartos.meetmethere.webservice.RetrofitUtil
import java.time.LocalDateTime

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
                    imageUrl = "imageUrl.com",
                    isPublic = it % 2 == 0,
                    createdByName = "User$it Doe",
                    createdById = it.toLong(),
                    updatedByName = "User${it + 1} Updater",
                    updatedById = 1 + it.toLong(),
                    startTime = LocalDateTime.of(2020, 8, 22, 2 + it, 0, 0),
                    endTime = LocalDateTime.of(2020, 8, 22, 3 + it, 0, 0)
                )
                add(item)
            }
        }


}