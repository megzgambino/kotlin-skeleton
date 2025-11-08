package com.example.service

import com.example.models.CreateUserRequest
import com.example.models.UpdateUserRequest
import com.example.models.User
import com.example.models.UserDTO
import com.example.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import java.time.LocalDateTime

class UserServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var cacheService: CacheService
    private lateinit var userService: UserService

    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setUp() {
        userRepository = mockk()
        cacheService = mockk(relaxed = true)
        userService = UserService(userRepository, cacheService)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `createUser persists and caches DTO`() = kotlinx.coroutines.test.runTest {
        val req = CreateUserRequest(name = "Alice", email = "alice@example.com", age = 30)
        val now = LocalDateTime.now()
        val created = User(id = 1, name = req.name, email = req.email, age = req.age, createdAt = now, updatedAt = now)
        coEvery { userRepository.create(req.name, req.email, req.age) } returns created
        every { cacheService.set(any(), any(), any()) } returns Unit

        val dto = userService.createUser(req)

        assertEquals(UserDTO(1, "Alice", "alice@example.com", 30), dto)
        val expectedKey = "user:1"
        val expectedVal = json.encodeToString(dto)
        verify { cacheService.set(expectedKey, expectedVal, any()) }
    }

    @Test
    fun `getUserById returns from cache when present`() = kotlinx.coroutines.test.runTest {
        val dto = UserDTO(2, "Bob", "bob@example.com", 25)
        every { cacheService.get("user:2") } returns json.encodeToString(dto)

        val result = userService.getUserById(2)

        assertEquals(dto, result)
        // ensure repository not called
        coVerify(exactly = 0) { userRepository.findById(any()) }
    }

    @Test
    fun `getUserById fetches repo and populates cache on miss`() = kotlinx.coroutines.test.runTest {
        every { cacheService.get("user:3") } returns null
        val now = LocalDateTime.now()
        val user = User(3, "Cara", "cara@example.com", 28, createdAt = now, updatedAt = now)
        coEvery { userRepository.findById(3) } returns user
        every { cacheService.set(any(), any(), any()) } returns Unit

        val result = userService.getUserById(3)

        val expectedDto = UserDTO(3, "Cara", "cara@example.com", 28)
        assertEquals(expectedDto, result)
        verify { cacheService.set("user:3", json.encodeToString(expectedDto), any()) }
    }

    @Test
    fun `getUserById returns null when not found`() = kotlinx.coroutines.test.runTest {
        every { cacheService.get("user:99") } returns null
        coEvery { userRepository.findById(99) } returns null

        val result = userService.getUserById(99)

        assertNull(result)
    }

    @Test
    fun `getAllUsers maps to DTOs`() = kotlinx.coroutines.test.runTest {
        val now = LocalDateTime.now()
        coEvery { userRepository.findAll() } returns listOf(
            User(1, "A", "a@ex.com", 20, createdAt = now, updatedAt = now),
            User(2, "B", "b@ex.com", 21, createdAt = now, updatedAt = now)
        )

        val list = userService.getAllUsers()

        assertEquals(
            listOf(
                UserDTO(1, "A", "a@ex.com", 20),
                UserDTO(2, "B", "b@ex.com", 21)
            ),
            list
        )
    }

    @Test
    fun `updateUser invalidates cache on success`() = kotlinx.coroutines.test.runTest {
        coEvery { userRepository.update(5, "Z", "z@ex.com", 40) } returns true
        every { cacheService.delete("user:5") } returns true

        val ok = userService.updateUser(5, UpdateUserRequest("Z", "z@ex.com", 40))

        assertTrue(ok)
        verify { cacheService.delete("user:5") }
    }

    @Test
    fun `updateUser does not touch cache on failure`() = kotlinx.coroutines.test.runTest {
        coEvery { userRepository.update(6, any(), any(), any()) } returns false

        val ok = userService.updateUser(6, UpdateUserRequest("Y", "y@ex.com", 41))

        assertFalse(ok)
        verify(exactly = 0) { cacheService.delete(any()) }
    }

    @Test
    fun `deleteUser invalidates cache on success`() = kotlinx.coroutines.test.runTest {
        coEvery { userRepository.delete(7) } returns true
        every { cacheService.delete("user:7") } returns true

        val ok = userService.deleteUser(7)

        assertTrue(ok)
        verify { cacheService.delete("user:7") }
    }

    @Test
    fun `deleteUser does not touch cache on failure`() = kotlinx.coroutines.test.runTest {
        coEvery { userRepository.delete(8) } returns false

        val ok = userService.deleteUser(8)

        assertFalse(ok)
        verify(exactly = 0) { cacheService.delete(any()) }
    }
}
