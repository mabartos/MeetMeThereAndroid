package org.mabartos.meetmethere.data.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class UserCredentials(val username: String, val password: String) : Parcelable {
}