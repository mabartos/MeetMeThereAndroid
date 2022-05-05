package org.mabartos.meetmethere.service.user

import org.mabartos.meetmethere.data.user.CreateUser
import org.mabartos.meetmethere.data.user.User
import java.util.*

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

            val adminOrganizedEvents: Set<Long> = mutableSetOf(0, 1)
            val adminAttributes: Map<String, String> =
                mutableMapOf(
                    Pair("phone", "+420 666 666 666"),
                    Pair("mobile", "+420 999 999 999")
                )
            add(
                User(
                    id = Int.MAX_VALUE.toLong(),
                    username = "admin",
                    email = "admin@admin",
                    password = "admin",
                    firstName = "Admin",
                    lastName = "Admin",
                    organizedEvents = adminOrganizedEvents,
                    attributes = adminAttributes
                )
            )
        }

    override fun findByUsername(username: String): User? =
        users.find { u -> u.username == username }

    override fun findById(id: Long): User? = users.find { u -> u.id == id }
    override fun findByEmail(email: String): User? = users.find { u -> u.email == email }

    override fun login(username: String, password: String): Boolean {
        val user = findByUsername(username)
        if (user != null && user.password == password) {
            this.token = UUID.randomUUID().toString()
            this.currentUser = user
            return true
        }
        return false
    }

    @Throws(ModelDuplicateException::class)
    override fun register(user: CreateUser): Boolean {
        if (findByUsername(user.username) != null) {
            throw ModelDuplicateException("This username is already in use", "username")
        }

        if (findByEmail(user.email) != null) {
            throw ModelDuplicateException("This email is already in use", "email")
        }

        val converted: User = mapCreateUserToUser(user.hashCode().toLong(), user)
        this.currentUser = converted
        users.add(converted)
        return true
    }

    override fun getInfo(): User? {
        return if (currentUser != null) currentUser else null
    }

    override fun updateUser(user: User) {
        val found = findById(user.id)
        if (found != null) {
            val index = users.indexOf(found)
            if (index != -1) {
                users[index] = user
                return
            }
        }
    }

    override fun logout() {
        this.token = null
        this.currentUser = null
    }

    override fun getCurrentUser(): User? {
        if (currentUser != null) {
            currentUser = findById(currentUser!!.id)
        }
        return currentUser
    }

    override fun addAttribute(userId: Long, key: String, value: String) {
        val user = findById(userId)
        if (user != null && !user.attributes.containsKey(key)) {
            val newMap = user.attributes.toMutableMap()
            newMap[key] = value
            updateUser(User.Builder(user).attributes(newMap).build())
        }
    }

    override fun removeAttribute(userId: Long, key: String) {
        val user = findById(userId)
        if (user != null && user.attributes.containsKey(key)) {
            val newMap = user.attributes.toMutableMap()
            newMap.remove(key)
            updateUser(User.Builder(user).attributes(newMap).build())
        }
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
}