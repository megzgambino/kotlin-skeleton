package com.example.plugins

import com.example.config.AppConfig
import com.example.service.CacheService
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

fun Application.configureRedis() {
    val config = AppConfig.fromEnvironment(environment)

    val poolConfig = JedisPoolConfig().apply {
        maxTotal = 10
        maxIdle = 5
        minIdle = 1
        testOnBorrow = true
        testOnReturn = true
        testWhileIdle = true
    }

    val jedisPool = JedisPool(poolConfig, config.redisHost, config.redisPort)

    install(Koin) {
        modules(module {
            single { jedisPool }
            single { CacheService(get()) }
        })
    }
}