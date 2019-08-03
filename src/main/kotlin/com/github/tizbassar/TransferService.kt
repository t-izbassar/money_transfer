package com.github.tizbassar

interface TransferService {

    suspend fun transfer(request: TransferRequest): TransferResult
}
