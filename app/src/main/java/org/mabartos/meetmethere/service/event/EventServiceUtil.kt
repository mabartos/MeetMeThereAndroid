package org.mabartos.meetmethere.service.event

class EventServiceUtil {

    companion object {
        private var service: EventService? = null

        fun createService(): EventService {
            if (service == null) {
                val isProductVersion: String? = System.getProperty("product", "")

                service = if (isProductVersion!!.isNotBlank() && "false" != isProductVersion) {
                    DefaultEventService()
                } else {
                    TestEventService()
                }
            }
            return service!!
        }

    }
}