package payment.command

import payment.domain.Payment

sealed class PaymentCommand
data class InitPaymentCommand(val payment: Payment) : PaymentCommand()