package com.github.tizbassar

import com.github.tizbassar.Controller.transfer
import io.ktor.application.Application
import io.ktor.server.netty.EngineMain

typealias AccountId = Long
typealias Money = Long

object App {
    private val transferService = DefaultTransferService()

    fun Application.transferModule() {
        transfer(transferService)
    }
}

fun main(args: Array<String>) {
    EngineMain.main(args)
}
