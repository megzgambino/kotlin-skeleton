package com.example.routes

import com.example.models.*
import com.example.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(userService: UserService) {
    route("/users") {
        // GET all users
        get {
            val users = userService.getAllUsers()
            call.respond(users)
        }

        // GET user by ID
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid user ID"))
                return@get
            }

            val user = userService.getUserById(id)
            if (user != null) {
                call.respond(user)
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
            }
        }

        // POST create new user
        post {
            try {
                val request = call.receive<CreateUserRequest>()
                val user = userService.createUser(request)
                call.respond(HttpStatusCode.Created, user)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // PUT update user
        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid user ID"))
                return@put
            }

            try {
                val request = call.receive<UpdateUserRequest>()
                val success = userService.updateUser(id, request)

                if (success) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "User updated successfully"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // DELETE user
        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid user ID"))
                return@delete
            }

            val success = userService.deleteUser(id)
            if (success) {
                call.respond(HttpStatusCode.OK, mapOf("message" to "User deleted successfully"))
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
            }
        }
    }
}