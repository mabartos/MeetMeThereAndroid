package org.mabartos.meetmethere.service

import android.os.Build
import androidx.annotation.RequiresApi

class EventServiceUtil {

    companion object {
        private var service: EventService? = null

        @RequiresApi(Build.VERSION_CODES.O)
        fun createService(): EventService {
            if (service == null) {
                service = TestEventService()
            }
            return service!!
        }

    }
}