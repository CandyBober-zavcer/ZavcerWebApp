package ru.yarsu.db

import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    private var initialized = false

    fun init() {
        if (initialized) return

        Database.connect(
            url = "jdbc:mysql://localhost/test",
            driver = "com.mysql.cj.jdbc.Driver",
            user = "root",
            password = "root"
        )
        initialized = true
    }

    // Для тестов
    fun initForTests(url: String, user: String, password: String) {
        Database.connect(
            url = url,
            driver = "com.mysql.cj.jdbc.Driver",
            user = user,
            password = password
        )
        initialized = true
    }
}