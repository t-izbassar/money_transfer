package com.github.tizbassar

import com.github.tizbassar.Controller.hello
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ControllerTest {

    @Test
    fun shouldGreet() = withTestApplication({
        hello()
    }) {
        with(handleRequest(HttpMethod.Get, "/hello")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Hello, World!", response.content)
        }
    }
}
