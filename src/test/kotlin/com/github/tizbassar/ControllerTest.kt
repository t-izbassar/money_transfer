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
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals

const val validFrom = 1L
const val validTo = 2L
const val validAmount = 100L
const val invalid = 999L
const val lockedFrom = 3L
const val lockedTo = 4L

internal class ControllerTest {

    private val transferService = mockk<TransferService>(relaxed = true)

    init {
        coEvery {
            transferService.transfer(TransferRequest(validFrom, validTo, validAmount))
        } returns TransferResult.Success(400, 700)
        coEvery {
            transferService.transfer(TransferRequest(validFrom, validTo, invalid))
        } returns TransferResult.NotEnoughBalance(500)
        coEvery {
            transferService.transfer(TransferRequest(invalid, validTo, validAmount))
        } returns TransferResult.SourceAccountNotFound
        coEvery {
            transferService.transfer(TransferRequest(validFrom, invalid, validAmount))
        } returns TransferResult.TargetAccountNotFound
        coEvery {
            transferService.transfer(TransferRequest(lockedFrom, validTo, validAmount))
        } returns TransferResult.SourceAccountLocked
        coEvery {
            transferService.transfer(TransferRequest(validFrom, lockedTo, validAmount))
        } returns TransferResult.TargetAccountLocked
    }

    @ParameterizedTest
    @CsvSource(
        "$validFrom, $validTo, $validAmount, 200",
        "$validFrom, $validTo, $invalid, 450",
        "$invalid, $validTo, $validAmount, 451",
        "$validFrom, $invalid, $validAmount, 453",
        "$lockedFrom, $validTo, $validAmount, 452",
        "$validFrom, $lockedTo, $validAmount, 454"
    )
    fun shouldConvertTransferResultsToAppropriateStatusCodes(
        from: AccountId, to: AccountId, amount: Money, statusCode: Int
    ) = withTestApplication({
        transfer(transferService)
    }) {
        val request = TransferRequest(from, to, amount)

        val call = requestTransfer(request)

        assertEquals(HttpStatusCode.fromValue(statusCode), call.response.status())
        coVerify {
            transferService.transfer(request)
        }
    }

    @ParameterizedTest
    @CsvSource(
        "$validFrom, $validTo, inv",
        "inv, $validTo, $validAmount",
        "$validFrom, inv, $validAmount",
        "$validFrom, $validTo, ",
        ", $validTo, $validAmount",
        "$validFrom, , $validAmount",
        ", , "
    )
    fun shouldFailIfRequestIsBad(from: String?, to: String?, amount: String?) = withTestApplication({
        transfer(transferService)
    }) {
        val call = requestTransfer(from, to, amount)

        with(call) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }
        coVerify(exactly = 0) {
            transferService.transfer(any())
        }
    }

    private fun TestApplicationEngine.requestTransfer(request: TransferRequest) =
        handleRequest(HttpMethod.Post, "/accounts/transfer") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf(
                "from" to request.from.toString(),
                "to" to request.to.toString(),
                "amount" to request.amount.toString()
            ).formUrlEncode())
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
