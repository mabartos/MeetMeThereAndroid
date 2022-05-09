package org.mabartos.meetmethere.service.user

import org.mabartos.meetmethere.data.user.CreateUser
import org.mabartos.meetmethere.data.user.User

interface UserService {

    companion object {
        val USERNAME_FIELD: String = "username"
        val EMAIL_FIELD: String = "email"
    }

    fun findById(id: Long, onSuccess: (User) -> Unit, onFailure: (Throwable) -> Unit)

    fun findByUsername(username: String, onSuccess: (User) -> Unit, onFailure: (Throwable) -> Unit)

    fun findByEmail(email: String, onSuccess: (User) -> Unit, onFailure: (Throwable) -> Unit)

    fun login(
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    )

    @Throws(ModelDuplicateException::class)
    fun register(user: CreateUser, onSuccess: () -> Unit, onFailure: (Throwable) -> Unit)

    fun updateUser(user: User, onSuccess: () -> Unit, onFailure: (Throwable) -> Unit)

    fun logout(onSuccess: () -> Unit, onFailure: (Throwable) -> Unit)

    fun getCurrentUser(onSuccess: (User) -> Unit, onFailure: (Throwable) -> Unit)

    fun addAttribute(
        userId: Long,
        key: String,
        value: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    )

    fun removeAttribute(
        userId: Long,
        key: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    )

    fun removeAttributes(
        userId: Long,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    )
}