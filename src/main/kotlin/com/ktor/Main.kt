package com.ktor


import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.google.gson.FieldNamingPolicy
import com.ktor.repository.BookRepository
import com.ktor.repository.DatabaseMigration
import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jetbrains.exposed.sql.Database
import org.slf4j.event.Level
import java.lang.reflect.Modifier
import java.util.*


open class SimpleJwt(val secret: String) {
    private val algorithm = Algorithm.HMAC256(secret)
    var verifier = JWT.require(algorithm).build()

    fun sing(name: String): String = JWT.create()
        .withClaim("name", name).sign(algorithm)
}

fun initConfig() {
    ConfigFactory.defaultApplication()
}

fun initDataBase() {
    val config = HikariConfig("/hikari.properties")
    val ds = HikariDataSource(config)
    Database.connect(ds)
}

fun migrateDastabase() {
    DatabaseMigration.migrate()
}

fun Application.module() {

    install(CallLogging) {
        level = Level.WARN
        mdc("executionId") {
            UUID.randomUUID().toString()
        }
    }

    install(ContentNegotiation) {
        gson {
            excludeFieldsWithModifiers(Modifier.TRANSIENT)
            setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        }
    }
    val simpleJwt = SimpleJwt("teste-jwt")

    install(Authentication) {
        jwt {
            verifier(simpleJwt.verifier)
            validate {
                UserIdPrincipal(it.payload.getClaim("name").toString())
            }
        }
    }

    initConfig()
    initDataBase()
    migrateDastabase()

    install(Routing) {
        book(BookRepository(), simpleJwt)
    }
}


class User(val name: String, var password: String)

val users = Collections.synchronizedMap(
    listOf(User("teste", "teste"))
        .associateBy { it.name }
        .toMutableMap()
)

fun main() {
    val port = ConfigFactory.load().getInt("ktor.deployment.port")
    embeddedServer(
        Netty, port,
        module = Application::module
    ).start(wait = true)
}