package org.mabartos.meetmethere.data.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Long,
    val username: String,
    val password: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val organizedEvents: Set<Long>,
    val attributes: Map<String, String>
) : Parcelable {
    fun getFullName(): String = "$firstName $lastName"

    class Builder(user: User) {
        private var id: Long = user.id
        private var username: String = user.username
        private var password: String = user.password
        private var email: String = user.email
        private var firstName: String = user.firstName
        private var lastName: String = user.lastName
        private var organizedEvents: Set<Long> = user.organizedEvents
        private var attributes: Map<String, String> = user.attributes

        fun username(username: String) = apply { this.username = username }
        fun password(password: String) = apply { this.password = password }
        fun email(email: String) = apply { this.email = email }
        fun firstName(firstName: String) = apply { this.firstName = firstName }
        fun lastName(lastName: String) = apply { this.lastName = lastName }
        fun organizedEvents(organizedEvents: Set<Long>) =
            apply { this.organizedEvents = organizedEvents }

        fun attributes(attributes: Map<String, String>) = apply { this.attributes = attributes }

        fun build() = User(
            id = id,
            username = username,
            password = password,
            email = email,
            firstName = firstName,
            lastName = lastName,
            organizedEvents = organizedEvents,
            attributes = attributes
        )
    }
}
