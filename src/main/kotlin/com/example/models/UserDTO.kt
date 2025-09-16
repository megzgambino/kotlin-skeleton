package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: Int? = null,
    val name: String,
    val email: String,
    val age: Int
)

@Serializable
data class CreateUserRequest(
    val name: String,
    val email: String,
    val age: Int
)

@Serializable
data class UpdateUserRequest(
    val name: String? = null,
    val email: String? = null,
    val age: Int? = null
)