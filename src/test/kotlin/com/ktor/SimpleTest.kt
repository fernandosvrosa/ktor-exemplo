package com.ktor

import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlin.test.Test
import kotlin.test.assertEquals


class SimpleTest {

    private fun libraryServer(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication(Application::module) {
            callback
        }
    }

    @Test
    fun sampleTest() = libraryServer {
        handleRequest(HttpMethod.Post, "/books") {
            addHeader("Content-Type", "application/json")
            setBody("""
                {
                  "title": "hi"
                }
                """.trimMargin())
        }.apply {
            assertEquals(HttpStatusCode.Unauthorized, response.status())
        }
    }
}