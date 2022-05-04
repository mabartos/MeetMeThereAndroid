package org.mabartos.meetmethere.service.user

import org.mabartos.meetmethere.data.user.CreateUser
import org.mabartos.meetmethere.data.user.User

interface UserService {

    fun findById(id: Long): User?

    fun findByUsername(username: String): User?

    fun findByEmail(email: String): User?

    fun login(username: String, password: String): String

    fun register(user: CreateUser): User?

    fun getInfo(): User?

    fun updateUser(user: User)

    fun logout()
}