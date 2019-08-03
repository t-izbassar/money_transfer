package com.github.tizbassar

data class TransferRequest(
    val from: AccountId,
    val to: AccountId,
    val amount: Money
)
