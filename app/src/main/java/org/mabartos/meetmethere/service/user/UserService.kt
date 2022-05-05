package org.mabartos.meetmethere.service.user

import org.mabartos.meetmethere.data.user.CreateUser
import org.mabartos.meetmethere.data.user.User

interface UserService {

    fun findById(id: Long): User?

    fun findByUsername(username: String): User?

    fun findByEmail(email: String): User?

    fun login(username: String, password: String): Boolean

    @Throws(ModelDuplicateException::class)
    fun register(user: CreateUser): Boolean

    fun getInfo(): User?

    fun updateUser(user: User)

    fun logout()

    fun getCurrentUser(): User?

    fun addAttribute(userId: Long, key: String, value: String);
    fun removeAttribute(userId: Long, key: String)
}