package org.mabartos.meetmethere.service.user

import org.mabartos.meetmethere.data.user.CreateUser
import org.mabartos.meetmethere.data.user.User
import org.mabartos.meetmethere.service.ModelNotFoundException
import org.mabartos.meetmethere.service.ServiceUtil
import org.mabartos.meetmethere.service.user.UserService.Companion.EMAIL_FIELD
import org.mabartos.meetmethere.service.user.UserService.Companion.USERNAME_FIELD
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

    override fun findById(id: Long, onSuccess: (User) -> Unit, onFailure: (Throwable) -> Unit) {
        ServiceUtil.callback(
            supplier = {
                users.find { u -> u.id == id } ?: throw ModelNotFoundException()
            }, onSuccess = {
                onSuccess.invoke(it)
            }, onFailure = {
                onFailure.invoke(it)
            })
    }

    override fun findByUsername(
        username: String,
        onSuccess: (User) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        ServiceUtil.callback(
            supplier = {
                users.find { u -> u.username == username } ?: throw ModelNotFoundException()
            }, onSuccess = {
                onSuccess.invoke(it)
            }, onFailure = {
                onFailure.invoke(it)
            })
    }

    override fun findByEmail(
        email: String,
        onSuccess: (User) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        ServiceUtil.callback(
            supplier = {
                users.find { u -> u.email == email } ?: throw ModelNotFoundException()
            }, onSuccess = {
                onSuccess.invoke(it)
            }, onFailure = {
                onFailure.invoke(it)
            })
    }

    override fun login(
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        findByUsername(username,
            onSuccess = { user ->
                if (user.password == password) {
                    this.token = UUID.randomUUID().toString()
                    this.currentUser = user
                    onSuccess.invoke()
                } else {
                    throw IllegalArgumentException("Invalid password")
                }
            }, onFailure = {
                onFailure.invoke(it)
            })
    }

    override fun register(
        user: CreateUser,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        findByUsername(user.username,
            onSuccess = {
                throw ModelDuplicateException(
                    "This username is already in use",
                    USERNAME_FIELD
                )
            },
            onFailure = {})

        findByEmail(user.email,
            onSuccess = {
                throw ModelDuplicateException(
                    "This email is already in use",
                    EMAIL_FIELD
                )
            },
            onFailure = {})

        val converted: User = mapCreateUserToUser(user.hashCode().toLong(), user)
        this.currentUser = converted
        users.add(converted)
        onSuccess.invoke()
    }

    override fun updateUser(user: User, onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
        findById(user.id,
            onSuccess = {
                val index = users.indexOf(it)
                if (index != -1) {
                    users[index] = user
                    onSuccess.invoke()
                } else {
                    throw ModelNotFoundException()
                }
            }, onFailure = {
                onFailure.invoke(it)
            })
    }

    override fun logout(onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
        this.token = null
        this.currentUser = null
        onSuccess.invoke()
    }

    override fun getCurrentUser(onSuccess: (User) -> Unit, onFailure: (Throwable) -> Unit) {
        ServiceUtil.callback(
            supplier = {
                if (currentUser != null) {
                    findById(
                        id = currentUser!!.id,
                        onSuccess = {
                            currentUser = it
                        }, onFailure = {
                            onFailure.invoke(it)
                        })
                }
            }, onSuccess = {
                onSuccess.invoke(currentUser!!)
            }, onFailure = {
                onFailure.invoke(it)
            })
    }

    override fun addAttribute(
        userId: Long,
        key: String,
        value: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        findById(userId,
            onSuccess = { user ->
                if (!user.attributes.containsKey(key)) {
                    val newMap = user.attributes.toMutableMap()
                    newMap[key] = value
                    updateUser(
                        User.Builder(user).attributes(newMap).build(),
                        onSuccess = { onSuccess.invoke() },
                        onFailure = { e -> onFailure.invoke(e) }
                    )
                }
            },
            onFailure = { onFailure.invoke(it) })
    }

    override fun removeAttribute(
        userId: Long,
        key: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        findById(userId,
            onSuccess = { user ->
                if (user.attributes.containsKey(key)) {
                    val newMap = user.attributes.toMutableMap()
                    newMap.remove(key)
                    updateUser(
                        User.Builder(user).attributes(newMap).build(),
                        onSuccess = { onSuccess.invoke() },
                        onFailure = { e -> onFailure.invoke(e) }
                    )
                }
            },
            onFailure = { onFailure.invoke(it) })
    }

    override fun removeAttributes(
        userId: Long,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        findById(userId,
            onSuccess = { user ->
                updateUser(
                    User.Builder(user).attributes(HashMap()).build(),
                    onSuccess = { onSuccess.invoke() },
                    onFailure = { e -> onFailure.invoke(e) }
                )
            },
            onFailure = { onFailure.invoke(it) })
    }
}