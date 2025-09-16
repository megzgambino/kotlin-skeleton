package com.example.service

import com.example.models.*
import com.example.repository.UserRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UserService(
    private val userRepository: UserRepository,
    private val cacheService: CacheService
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val cacheKeyPrefix = "user:"
    private val cacheExpireSeconds = 300 // 5 minutes

    suspend fun createUser(request: CreateUserRequest): UserDTO {
        val user = userRepository.create(request.name, request.email, request.age)
        val userDto = UserDTO(user.id, user.name, user.email, user.age)

        // Cache the new user
        cacheService.set(
            "$cacheKeyPrefix${user.id}",
            json.encodeToString(userDto),
            cacheExpireSeconds
        )

        return userDto
    }

    suspend fun getUserById(id: Int): UserDTO? {
        // Try cache first
        val cacheKey = "$cacheKeyPrefix$id"
        val cachedUser = cacheService.get(cacheKey)

        if (cachedUser != null) {
            return json.decodeFromString<UserDTO>(cachedUser)
        }

        // Fetch from database
        val user = userRepository.findById(id) ?: return null
        val userDto = UserDTO(user.id, user.name, user.email, user.age)

        // Update cache
        cacheService.set(cacheKey, json.encodeToString(userDto), cacheExpireSeconds)

        return userDto
    }

    suspend fun getAllUsers(): List<UserDTO> {
        // For list operations, we could implement more sophisticated caching
        val users = userRepository.findAll()
        return users.map { UserDTO(it.id, it.name, it.email, it.age) }
    }

    suspend fun updateUser(id: Int, request: UpdateUserRequest): Boolean {
        val success = userRepository.update(id, request.name, request.email, request.age)

        if (success) {
            // Invalidate cache
            cacheService.delete("$cacheKeyPrefix$id")
        }

        return success
    }

    suspend fun deleteUser(id: Int): Boolean {
        val success = userRepository.delete(id)

        if (success) {
            // Invalidate cache
            cacheService.delete("$cacheKeyPrefix$id")
        }

        return success
    }
}