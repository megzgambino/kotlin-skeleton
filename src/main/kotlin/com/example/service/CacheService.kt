package com.example.service

import redis.clients.jedis.JedisPool

class CacheService(private val jedisPool: JedisPool) {

    fun get(key: String): String? {
        return jedisPool.resource.use { jedis ->
            jedis.get(key)
        }
    }

    fun set(key: String, value: String, expireSeconds: Int = 300) {
        jedisPool.resource.use { jedis ->
            jedis.setex(key, expireSeconds.toLong(), value)
        }
    }

    fun delete(key: String): Boolean {
        return jedisPool.resource.use { jedis ->
            jedis.del(key) > 0
        }
    }

    fun exists(key: String): Boolean {
        return jedisPool.resource.use { jedis ->
            jedis.exists(key)
        }
    }
}