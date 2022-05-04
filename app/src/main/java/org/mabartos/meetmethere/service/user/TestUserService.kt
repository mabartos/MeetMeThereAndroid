package org.mabartos.meetmethere.service.user

import org.mabartos.meetmethere.data.user.CreateUser
import org.mabartos.meetmethere.data.user.User

class TestUserService : UserService {

    private val users: MutableList<User> = getMockedUsers()
    private var token: String? = null
    private var currentUser: User? = null

    private fun getMockedUsers(count: Int = 10) =
        mutableListOf<User>().apply {
            repeat(count) {
                val user = User(
                    id = it.toLong(),
                    username = "User $it",
                    email = "user${it}@mail.com",
                    password = "pass",
                    firstName = "John",
                    lastName = "Doe",
                    organizedEvents = HashSet(),
                    attributes = HashMap()
                )
                add(user)
            }
        }

    override fun findByUsername(username: String): User? =
        users.find { u -> u.username == username }

    override fun findById(id: Long): User? = users.find { u -> u.id == id }
    override fun findByEmail(email: String): User? = users.find { u -> u.email == email }

    override fun login(username: String, password: String): String {
        val user = findByUsername(username)
        if (user != null && user.password == password) {
            this.token = "token123"
            this.currentUser = user
            return token!!
        }
        return ""
    }

    override fun register(user: CreateUser): User? {
        if (findByUsername(user.username) == null && findByEmail(user.email) == null) {
            val converted: User = mapCreateUserToUser(user.hashCode().toLong(), user)
            users.add(converted)
            return converted
        }
        return null
    }

    override fun getInfo(): User? {
        return if (currentUser != null) currentUser else null
    }

    override fun updateUser(user: User) {
        //findById(user.id)
    }

    override fun logout() {
        this.token = null
        this.currentUser = null
    }

    private fun mapCreateUserToUser(id: Long, createUser: CreateUser): User {
        return User(
            id = id,
            username = createUser.username,
            password = createUser.password,
            email = createUser.email,
            firstName = createUser.firstName,
            lastName = createUser.lastName,
            organizedEvents = HashSet(),
            attributes = HashMap()
        )
    }

    private fun isInvalidIndex(id: Long): Boolean {
        return id < 0 || id >= users.size
    }
}