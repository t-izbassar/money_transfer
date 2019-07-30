package com.github.tizbassar

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing

object Controller {

    fun Application.hello() {
        routing {
            get("/hello") {
                call.respondText("Hello, World!")
            }
        }
    }
}
