package payment.domain

import java.math.BigDecimal
import java.util.*


data class Payment internal constructor(
    val originAccount: String,
    val destinationAccount: String,
    val amount: BigDecimal,
    val id: UUID = UUID.randomUUID(),
    val alreadyExecuted: Boolean = false
) {
    companion object {
        fun create(originAccount: String, destinationAccount: String, amount: BigDecimal) {
            when {
                originAccount == destinationAccount -> throw IllegalArgumentException("Account cant be the same")
                amount <= BigDecimal.ZERO -> throw IllegalArgumentException("Amount should be greater than zero")
                else -> Payment(originAccount, destinationAccount, amount)
            }
        }
    }

}