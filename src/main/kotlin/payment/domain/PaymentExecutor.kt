package payment.domain

import java.time.Instant

interface PaymentExecutor {
    operator fun invoke(payment: Payment): Response
    data class Response(val valueDate: Instant)
}