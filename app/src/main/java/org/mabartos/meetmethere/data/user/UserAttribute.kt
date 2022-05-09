package org.mabartos.meetmethere.data.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class UserAttribute(val key: String, val value: String) : Parcelable {
}