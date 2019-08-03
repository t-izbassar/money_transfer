package com.github.tizbassar

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing

object Controller {

    fun Application.transfer(transferService: TransferService) {
        routing {
            post("/accounts/transfer") {
                val parameters = call.receiveParameters()
                val request = validate(parameters["from"], parameters["to"], parameters["amount"])
                if (request == null) {
                    call.respond(HttpStatusCode.BadRequest)
                } else {
                    when (transferService.transfer(request)) {
                        is TransferResult.Success ->
                            call.respond(HttpStatusCode.OK)
                        is TransferResult.NotEnoughBalance ->
                            call.respond(HttpStatusCode.notEnoughBalance)
                        is TransferResult.SourceAccountNotFound ->
                            call.respond(HttpStatusCode.sourceAccountNotFound)
                        is TransferResult.SourceAccountLocked ->
                            call.respond(HttpStatusCode.sourceAccountLocked)
                        is TransferResult.TargetAccountNotFound ->
                            call.respond(HttpStatusCode.targetAccountNotFound)
                        is TransferResult.TargetAccountLocked ->
                            call.respond(HttpStatusCode.targetAccountLocked)
                    }
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

val HttpStatusCode.Companion.notEnoughBalance: HttpStatusCode
    get() = HttpStatusCode(450, "Not enough balance")

val HttpStatusCode.Companion.sourceAccountNotFound: HttpStatusCode
    get() = HttpStatusCode(451, "Source account not found")

val HttpStatusCode.Companion.sourceAccountLocked: HttpStatusCode
    get() = HttpStatusCode(452, "Source account locked")

val HttpStatusCode.Companion.targetAccountNotFound: HttpStatusCode
    get() = HttpStatusCode(453, "Target account not found")

val HttpStatusCode.Companion.targetAccountLocked: HttpStatusCode
    get() = HttpStatusCode(454, "Target account locked")
