package org.mabartos.meetmethere.service.user

import org.mabartos.meetmethere.data.user.CreateUser
import org.mabartos.meetmethere.data.user.User
import org.mabartos.meetmethere.data.user.UserAttribute
import org.mabartos.meetmethere.data.user.UserCredentials
import org.mabartos.meetmethere.service.ModelNotFoundException
import org.mabartos.meetmethere.webservice.RetrofitUtil
import org.mabartos.meetmethere.webservice.UsersApi

class DefaultUserService(
    private val usersApi: UsersApi = RetrofitUtil.createAqiWebService()
) : UserService {

    private var token: String = ""
    private var currentUser: User? = null

    override fun findById(id: Long, onSuccess: (User) -> Unit, onFailure: (Throwable) -> Unit) {
        RetrofitUtil.callback(
            supplier = {
                usersApi.getById(id, token)
            }, onSuccess = { list ->
                onSuccess.invoke(list)
            }, onFailure = { e ->
                onFailure.invoke(e)
            })
    }

    override fun findByUsername(
        username: String,
        onSuccess: (User) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        RetrofitUtil.callback(
            supplier = {
                usersApi.getByUsername(username, token)
            }, onSuccess = { list ->
                onSuccess.invoke(list)
            }, onFailure = { e ->
                onFailure.invoke(e)
            })
    }

    override fun findByEmail(
        email: String,
        onSuccess: (User) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        RetrofitUtil.callback(
            supplier = {
                usersApi.getByEmail(email, token)
            }, onSuccess = { list ->
                onSuccess.invoke(list)
            }, onFailure = { e ->
                onFailure.invoke(e)
            })
    }

    override fun login(
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        RetrofitUtil.callback(
            supplier = {
                usersApi.login(UserCredentials(username, password))
            }, onSuccess = {
                token = it
                onSuccess.invoke()
            }, onFailure = { e ->
                onFailure.invoke(e)
            })
    }

    override fun register(
        user: CreateUser,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        RetrofitUtil.callback(
            supplier = {
                usersApi.register(user)
            }, onSuccess = {
                token = it
                onSuccess.invoke()
            }, onFailure = { e ->
                onFailure.invoke(e)
            })
    }

    override fun getCurrentUser(onSuccess: (User) -> Unit, onFailure: (Throwable) -> Unit) {
        RetrofitUtil.callback(
            supplier = {
                val user = currentUser ?: throw ModelNotFoundException()
                usersApi.getById(user.id, token)
            }, onSuccess = {
                onSuccess.invoke(it)
            }, onFailure = { e ->
                onFailure.invoke(e)
            })
    }

    override fun updateUser(user: User, onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
        RetrofitUtil.callback(
            supplier = {
                usersApi.updateUser(user.id, user, token)
            }, onSuccess = {
                onSuccess.invoke()
            }, onFailure = { e ->
                onFailure.invoke(e)
            })
    }

    override fun logout(onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
        this.token = ""
        this.currentUser = null
        onSuccess.invoke()
    }

    override fun addAttribute(
        userId: Long,
        key: String,
        value: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        RetrofitUtil.callback(
            supplier = {
                usersApi.addAttribute(userId, listOf(UserAttribute(key, value)), token)
            }, onSuccess = {
                onSuccess.invoke()
            }, onFailure = { e ->
                onFailure.invoke(e)
            })
    }

    override fun removeAttribute(
        userId: Long,
        key: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        RetrofitUtil.callback(
            supplier = {
                usersApi.removeAttribute(userId, key, token)
            }, onSuccess = {
                onSuccess.invoke()
            }, onFailure = { e ->
                onFailure.invoke(e)
            })
    }

    override fun removeAttributes(
        userId: Long,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        RetrofitUtil.callback(
            supplier = {
                usersApi.removeAttributes(userId, token)
            }, onSuccess = {
                onSuccess.invoke()
            }, onFailure = { e ->
                onFailure.invoke(e)
            })
    }
}