package com.rioaki.ocrdemo.plugins

import com.rioaki.ocrdemo.db.OcrJobTable
import io.ktor.server.application.*
import java.sql.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/ocr_db",
        user = "postgres",
        password = "postgres"
    )
    transaction {
        SchemaUtils.create(OcrJobTable)
    }
}

fun Application.connectToPostgres(embedded: Boolean): Connection {
    Class.forName("org.postgresql.Driver")
    if (embedded) {
        return DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "root", "")
    } else {
        val url = environment.config.property("postgres.url").getString()
        val user = environment.config.property("postgres.user").getString()
        val password = environment.config.property("postgres.password").getString()

        return DriverManager.getConnection(url, user, password)
    }
}