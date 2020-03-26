package com.ktor.repository

import org.flywaydb.core.Flyway
import java.util.*

object DatabaseMigration {
    fun migrate(){
        val config =  Properties()
        config.load(DatabaseMigration.javaClass.classLoader.getResourceAsStream ("hikari.properties"))
       val flyway = Flyway.configure()
            .dataSource(config.getProperty("dataSource.url"),
                config.getProperty("dataSource.user"),
                config.getProperty("dataSource.password"))
            .schemas("test")
            .locations("db/migration").load()
        flyway.migrate()

    }
}