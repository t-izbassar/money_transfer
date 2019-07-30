package com.github.tizbassar

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing

object Controller {

    fun Application.transfer() {
        routing {
            post("/accounts/transfer") {
                val parameters = call.receiveParameters()
                val request = validate(parameters["from"], parameters["to"], parameters["amount"])
                if (request == null) {
                    call.respond(HttpStatusCode.BadRequest)
                } else {
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }

    private fun validate(from: String?, to: String?, amount: String?): TransferRequest? {
        return if (from == null || to == null || amount == null) {
            null
        } else if (from.toLongOrNull() == null || to.toLongOrNull() == null || amount.toLongOrNull() == null) {
            null
        } else {
            TransferRequest(from.toLong(), to.toLong(), amount.toLong())
        }
    }
}
