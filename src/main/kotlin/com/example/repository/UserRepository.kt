package com.example.repository

import com.example.models.User

interface UserRepository {
    suspend fun create(name: String, email: String, age: Int): User
    suspend fun findById(id: Int): User?
    suspend fun findByEmail(email: String): User?
    suspend fun findAll(): List<User>
    suspend fun update(id: Int, name: String?, email: String?, age: Int?): Boolean
    suspend fun delete(id: Int): Boolean
}