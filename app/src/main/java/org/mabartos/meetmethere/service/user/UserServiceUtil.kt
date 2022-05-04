package org.mabartos.meetmethere.service.user

class UserServiceUtil {

    companion object {
        private var service: UserService? = null

        fun createService(): UserService {
            if (service == null) {
                service = TestUserService()
            }
            return service!!
        }

    }

}