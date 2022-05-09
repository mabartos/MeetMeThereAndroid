package org.mabartos.meetmethere.webservice

import org.mabartos.meetmethere.data.user.CreateUser
import org.mabartos.meetmethere.data.user.User
import org.mabartos.meetmethere.data.user.UserAttribute
import org.mabartos.meetmethere.data.user.UserCredentials
import retrofit2.Call
import retrofit2.http.*

interface UsersApi {

    @GET("users/@{id}/")
    fun getById(
        @Path("id") id: Long,
        @Header("Authorization") token: String,
    ): Call<User>

    @GET("users/username/@{username}")
    fun getByUsername(
        @Path("username") username: String,
        @Header("Authorization") token: String,
    ): Call<User>

    @GET("users/email/@{email}")
    fun getByEmail(
        @Path("email") email: String,
        @Header("Authorization") token: String,
    ): Call<User>

    @DELETE("users/@{id}")
    fun removeUser(id: Long, @Header("Authorization") token: String): Call<Unit>

    @POST("users/")
    fun createUser(@Body user: CreateUser, @Header("Authorization") token: String): Call<Long>

    @PATCH("users/@{id}/")
    fun updateUser(
        @Path("id") id: Long,
        @Body user: User,
        @Header("Authorization") token: String
    ): Call<Long>

    @POST("users/@{id}/attributes")
    fun addAttribute(
        @Path("id") id: Long,
        @Body attributes: List<UserAttribute>,
        @Header("Authorization") token: String
    ): Call<Unit>

    @DELETE("users/@{id}/attributes/@{key}")
    fun removeAttribute(
        @Path("id") id: Long,
        @Path("key") key: String,
        @Header("Authorization") token: String
    ): Call<Unit>

    @DELETE("users/@{id}/attributes")
    fun removeAttributes(@Path("id") id: Long, @Header("Authorization") token: String): Call<Unit>

    @POST("login")
    fun login(@Body credentials: UserCredentials): Call<String>

    @POST("register")
    fun register(@Body user: CreateUser): Call<String>
}