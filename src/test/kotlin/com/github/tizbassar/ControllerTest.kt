package com.github.tizbassar

import com.github.tizbassar.Controller.transfer
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.formUrlEncode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals

internal class ControllerTest {

    @Test
    @Disabled
    fun shouldTransferSuccessfully() = withTestApplication({
        transfer()
    }) {
        val call = requestTransfer("1", "2", "100")

        with(call) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Transferred 100 from 1 to 2", response.content)
        }
    }

    @Test
    @Disabled
    fun shouldFailToTransferIfNotEnoughBalance() = withTestApplication({
        transfer()
    }) {
        val call = requestTransfer("1", "2", "999")

        with(call) {
            assertEquals(HttpStatusCode.fromValue(422), response.status())
            assertEquals("Not enough balance in account 1 to transfer 999", response.content)
        }
    }

    @ParameterizedTest
    @Disabled
    @CsvSource(
            "999, 2",
            "1, 999",
            "999, 999"
    )
    fun shouldFailToTransferIfAccountNotFound(from: String?, to: String?) = withTestApplication({
        transfer()
    }) {
        val call = requestTransfer(from, to, "100")

        with(call) {
            assertEquals(HttpStatusCode.NotFound, response.status())
        }
    }

    @ParameterizedTest
    @CsvSource(
            "1, 2, inv",
            "inv, 2, 100",
            "1, inv, 100",
            "1, 2, ",
            ", 2, 100",
            "1, , 100",
            ", , "
    )
    fun shouldFailIfRequestIsBad(from: String?, to: String?, amount: String?) = withTestApplication({
        transfer()
    }) {
        val call = requestTransfer(from, to, amount)

        with(call) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }
    }

    private fun TestApplicationEngine.requestTransfer(from: String?, to: String?, amount: String?) =
            handleRequest(HttpMethod.Post, "/accounts/transfer") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(listOf(
                        "from" to from,
                        "to" to to,
                        "amount" to amount
                ).formUrlEncode())
            }
}
