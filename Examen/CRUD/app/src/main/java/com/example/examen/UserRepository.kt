package com.example.examen

import java.io.File

class UserRepository(private val fileName: String) {

    init {
        val file = File(fileName)
        if (!file.exists()) file.createNewFile()
        if (!file.canWrite()) {
            throw IllegalStateException("No se puede escribir en el archivo $fileName")
        }
    }

    fun addUser(user: User) {
        try {
            val file = File(fileName)
            file.appendText("${user.id},${user.email},${user.birthday},${user.country},${user.preferences},${user.income},${user.password},${user.isPremium ?: "null"}\n")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error al guardar el usuario: ${e.message}")
        }
    }

    fun getAllUsers(): List<User> {
        val file = File(fileName)
        if (!file.exists()) return emptyList()

        return file.readLines().mapNotNull { line ->
            val fields = line.split(",")
            if (fields.size < 7) null else User(
                id = fields[0],
                email = fields[1],
                birthday = fields[2],
                country = fields[3],
                preferences = fields[4],
                income = fields[5].toDouble(),
                password = fields[6],
                isPremium = if (fields[7] == "null") null else fields[7]
            )
        }
    }

    fun getUserById(id: String): User? {
        return getAllUsers().find { it.id == id }
    }

    fun updateUser(user: User) {
        val users = getAllUsers().map {
            if (it.id == user.id) user else it
        }
        saveAllUsers(users)
    }

    fun deleteUser(id: String) {
        val users = getAllUsers().filterNot { it.id == id }
        saveAllUsers(users)
    }

    fun saveAllUsers(users: List<User>) {
        val file = File(fileName)
        file.writeText("") // Limpiar contenido
        users.forEach { user ->
            file.appendText("${user.id},${user.email},${user.birthday},${user.country},${user.preferences},${user.income},${user.password},${user.isPremium ?: "null"}\n")
        }
    }
}

