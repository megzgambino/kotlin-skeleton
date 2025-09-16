package com.example.repository

import com.example.models.User
import com.example.plugins.DatabaseFactory.dbQuery
import com.example.plugins.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

class UserRepositoryImpl : UserRepository {

    private fun resultRowToUser(row: ResultRow) = User(
        id = row[Users.id],
        name = row[Users.name],
        email = row[Users.email],
        age = row[Users.age],
        createdAt = row[Users.createdAt],
        updatedAt = row[Users.updatedAt]
    )

    override suspend fun create(name: String, email: String, age: Int): User = dbQuery {
        val insertStatement = Users.insert {
            it[Users.name] = name
            it[Users.email] = email
            it[Users.age] = age
            it[Users.createdAt] = LocalDateTime.now()
            it[Users.updatedAt] = LocalDateTime.now()
        }

        val resultRow = insertStatement.resultedValues?.singleOrNull()
            ?: throw Exception("Failed to create user")

        resultRowToUser(resultRow)
    }

    override suspend fun findById(id: Int): User? = dbQuery {
        Users.select { Users.id eq id }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun findByEmail(email: String): User? = dbQuery {
        Users.select { Users.email eq email }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun findAll(): List<User> = dbQuery {
        Users.selectAll()
            .map(::resultRowToUser)
    }

    override suspend fun update(id: Int, name: String?, email: String?, age: Int?): Boolean = dbQuery {
        Users.update({ Users.id eq id }) {
            name?.let { newName -> it[Users.name] = newName }
            email?.let { newEmail -> it[Users.email] = newEmail }
            age?.let { newAge -> it[Users.age] = newAge }
            it[Users.updatedAt] = LocalDateTime.now()
        } > 0
    }

    override suspend fun delete(id: Int): Boolean = dbQuery {
        Users.deleteWhere { Users.id eq id } > 0
    }
}