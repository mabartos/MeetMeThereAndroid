package org.mabartos.meetmethere.service.user

import android.os.Build
import androidx.annotation.RequiresApi

class UserServiceUtil {

    companion object {
        private var service: UserService? = null

        @RequiresApi(Build.VERSION_CODES.O)
        fun createService(): UserService {
            if (service == null) {
                service = TestUserService()
            }
            return service!!
        }

    }

}