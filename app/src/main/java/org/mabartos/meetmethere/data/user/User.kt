package org.mabartos.meetmethere.data.user

data class User(
    val id: Long,
    val username: String,
    val password: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val organizedEvents: Set<Long>,
    val attributes: Map<String, String>
)
