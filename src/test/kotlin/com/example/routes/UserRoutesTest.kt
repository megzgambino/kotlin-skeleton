package com.example.routes

import com.example.models.CreateUserRequest
import com.example.models.UserDTO
import com.example.service.UserService
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

class UserRoutesTest {

    private val userService: UserService = mockk()

    private fun Application.testModule() {
        routing {
            userRoutes(userService)
        }
    }

    @Test
    fun `GET all users returns list`() = testApplication {
        val users = listOf(
            UserDTO(id = 1, name = "John", email = "john@example.com", age = 30),
            UserDTO(id = 2, name = "Jane", email = "jane@example.com", age = 25)
        )
        coEvery { userService.getAllUsers() } returns users

        application {
            testModule()
        }

        val response = client.get("/users")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET user by id returns BadRequest for invalid id`() = testApplication {
        application { testModule() }

        val response = client.get("/users/abc")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `GET user by id returns user when found`() = testApplication {
        val user = UserDTO(id = 1, name = "John", email = "john@example.com", age = 30)
        coEvery { userService.getUserById(1) } returns user

        application { testModule() }

        val response = client.get("/users/1")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET user by id returns NotFound when user does not exist`() = testApplication {
        coEvery { userService.getUserById(1) } returns null

        application { testModule() }

        val response = client.get("/users/1")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `POST create user returns Created`() = testApplication {
        val request = CreateUserRequest(name = "John", email = "john@example.com", age = 30)
        val createdUser = UserDTO(id = 1, name = request.name, email = request.email, age = request.age)

        coEvery { userService.createUser(request) } returns createdUser

        application { testModule() }

        val response = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"John","email":"john@example.com","age":30}""")
        }

        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun `POST create user returns BadRequest on exception`() = testApplication {
        val requestJson = """{"name":"John","email":"john@example.com","age":30}"""

        coEvery { userService.createUser(any()) } throws IllegalArgumentException("Invalid data")

        application { testModule() }

        val response = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(requestJson)
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `PUT update user returns BadRequest for invalid id`() = testApplication {
        application { testModule() }

        val response = client.put("/users/abc") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `PUT update user returns NotFound when service returns false`() = testApplication {
        coEvery { userService.updateUser(eq(1), any()) } returns false

        application { testModule() }

        val response = client.put("/users/1") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `PUT update user returns OK when successful`() = testApplication {
        coEvery { userService.updateUser(eq(1), any()) } returns true

        application { testModule() }

        val response = client.put("/users/1") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `PUT update user returns BadRequest on exception`() = testApplication {
        coEvery { userService.updateUser(eq(1), any()) } throws IllegalArgumentException("Invalid data")

        application { testModule() }

        val response = client.put("/users/1") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `DELETE user returns BadRequest for invalid id`() = testApplication {
        application { testModule() }

        val response = client.delete("/users/abc")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `DELETE user returns NotFound when service returns false`() = testApplication {
        coEvery { userService.deleteUser(1) } returns false

        application { testModule() }

        val response = client.delete("/users/1")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `DELETE user returns OK when successful`() = testApplication {
        coEvery { userService.deleteUser(1) } returns true

        application { testModule() }

        val response = client.delete("/users/1")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
