package com.example.service

import com.example.service.CacheService
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase.*
import org.junit.After
import org.junit.Before
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import kotlin.test.Test

private lateinit var jedisPool: JedisPool
private lateinit var jedis: Jedis
private lateinit var cacheService: CacheService

class CacheServiceTest {
    @Before
    fun setUp() {
        jedisPool = mockk(relaxed = true)
        jedis = mockk(relaxed = true)
        every { jedisPool.resource } returns jedis
        cacheService = CacheService(jedisPool)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `get returns stored value`() {
        every { jedis.get("key1") } returns "value1"

        val result = cacheService.get("key1")

        assertEquals("value1", result)
        verify { jedis.get("key1") }
    }

    @Test
    fun `get returns null when key not found`() {
        every { jedis.get("missing") } returns null

        val result = cacheService.get("missing")

        assertNull(result)
        verify { jedis.get("missing") }
    }

    @Test
    fun `set stores value with expiry`() {
        every { jedis.setex("key2", 300L, "val2") } returns "OK"

        cacheService.set("key2", "val2", 300)

        verify { jedis.setex("key2", 300L, "val2") }
    }

    @Test
    fun `set stores value with default expiry`() {
        every { jedis.setex("key3", any(), "val3") } returns "OK"

        cacheService.set("key3", "val3")

        verify { jedis.setex("key3", 300L, "val3") }
    }

    @Test
    fun `delete returns true when key existed`() {
        every { jedis.del("key4") } returns 1

        val deleted = cacheService.delete("key4")

        assertTrue(deleted)
        verify { jedis.del("key4") }
    }

    @Test
    fun `delete returns false when key missing`() {
        every { jedis.del("key5") } returns 0

        val deleted = cacheService.delete("key5")

        assertFalse(deleted)
        verify { jedis.del("key5") }
    }

    @Test
    fun `exists returns true when key exists`() {
        every { jedis.exists("key6") } returns true

        val res = cacheService.exists("key6")

        assertTrue(res)
        verify { jedis.exists("key6") }
    }

    @Test
    fun `exists returns false when key does not exist`(): Unit {
        every { jedis.exists("key7") } returns false

        val res = cacheService.exists("key7")

        assertFalse(res)
        verify { jedis.exists("key7") }

    }
}