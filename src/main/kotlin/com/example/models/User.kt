package com.example.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val age: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)