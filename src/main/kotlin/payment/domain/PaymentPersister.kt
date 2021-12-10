package payment.domain

interface PaymentPersister {
    operator fun invoke(payment: List<Payment>): Unit
}