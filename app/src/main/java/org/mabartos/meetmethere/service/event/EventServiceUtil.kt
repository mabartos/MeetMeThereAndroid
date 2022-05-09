package org.mabartos.meetmethere.service.event

class EventServiceUtil {

    companion object {
        private var service: EventService? = null

        fun createService(): EventService {
            return createService(null)
        }

        fun createService(token: String?): EventService {
            if (service == null) {
                if (token == null) return TestEventService()
                val isProductVersion: String? = System.getProperty("product", "")

                service = if (isProductVersion!!.isNotBlank() || "false" != isProductVersion) {
                    DefaultEventService(token)
                } else {
                    TestEventService()
                }
            }
            return service!!
        }

    }
}