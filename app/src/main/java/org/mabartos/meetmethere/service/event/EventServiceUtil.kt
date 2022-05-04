package org.mabartos.meetmethere.service.event

class EventServiceUtil {

    companion object {
        private var service: EventService? = null

        fun createService(): EventService {
            if (service == null) {
                service = TestEventService()
            }
            return service!!
        }

    }
}