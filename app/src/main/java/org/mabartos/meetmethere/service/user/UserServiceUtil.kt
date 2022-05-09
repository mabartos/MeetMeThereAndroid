package org.mabartos.meetmethere.service.user

class UserServiceUtil {

    companion object {
        private var service: UserService? = null

        fun createService(): UserService {
            if (service == null) {
                val isProductVersion: String? = System.getProperty("product", "")

                service = if (isProductVersion!!.isNotBlank() && "false" != isProductVersion) {
                    DefaultUserService()
                } else {
                    TestUserService()
                }
            }
            return service!!
        }

    }

}