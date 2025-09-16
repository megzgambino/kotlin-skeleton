package com.example.config

import io.ktor.server.application.*

data class AppConfig(
    val dbHost: String,
    val dbPort: Int,
    val dbName: String,
    val dbUser: String,
    val dbPassword: String,
    val redisHost: String,
    val redisPort: Int
) {
    companion object {
        fun fromEnvironment(environment: ApplicationEnvironment): AppConfig {
            val config = environment.config

            return AppConfig(
                dbHost = config.propertyOrNull("database.host")?.getString() ?: "localhost",
                dbPort = config.propertyOrNull("database.port")?.getString()?.toInt() ?: 5432,
                dbName = config.propertyOrNull("database.name")?.getString() ?: "postgres",
                dbUser = config.propertyOrNull("database.user")?.getString() ?: "postgres",
                dbPassword = config.propertyOrNull("database.password")?.getString() ?: "postgres",
                redisHost = config.propertyOrNull("redis.host")?.getString() ?: "localhost",
                redisPort = config.propertyOrNull("redis.port")?.getString()?.toInt() ?: 6379
            )
        }
    }
}