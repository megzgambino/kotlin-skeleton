package com.example.plugins

import com.example.repository.UserRepositoryImpl
import com.example.routes.userRoutes
import com.example.service.CacheService
import com.example.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val cacheService by inject<CacheService>()
    val userRepository = UserRepositoryImpl()
    val userService = UserService(userRepository, cacheService)

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (cause.message ?: "Unknown error")))
        }
    }

    routing {
        route("/api") {
            userRoutes(userService)
        }

        get("/health") {
            call.respond(mapOf("status" to "healthy"))
        }
    }
}