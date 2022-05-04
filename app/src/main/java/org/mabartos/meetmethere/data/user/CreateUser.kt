package org.mabartos.meetmethere.data.user

data class CreateUser(
    val username: String,
    val password: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val attributes: Map<String, String>
)
