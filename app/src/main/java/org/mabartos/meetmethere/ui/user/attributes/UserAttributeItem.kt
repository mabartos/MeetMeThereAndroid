package org.mabartos.meetmethere.ui.user.attributes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class UserAttributeItem(
    val key: String,
    val value: String
) : Parcelable {
}