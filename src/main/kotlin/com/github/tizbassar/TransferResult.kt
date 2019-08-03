package com.github.tizbassar

sealed class TransferResult {

    data class Success(
        val fromBalance: Money,
        val toBalance: Money
    ) : TransferResult()

    data class NotEnoughBalance(
        val fromBalance: Money
    ) : TransferResult()

    object SourceAccountNotFound : TransferResult()

    object TargetAccountNotFound : TransferResult()

    object SourceAccountLocked : TransferResult()

    object TargetAccountLocked : TransferResult()
}
