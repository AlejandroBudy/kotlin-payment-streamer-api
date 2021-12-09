package payment.eventsourcing

import payment.domain.Payment
import java.time.Instant

sealed class PaymentState : EntityState<PaymentEvent> {
    abstract override fun receive(event: PaymentEvent): PaymentState
}

object InitialState : PaymentState() {
    override fun receive(event: PaymentEvent): PaymentState = when (event) {
        is PaymentInitiated -> register(event.id, event.paymentList)
        else -> this
    }
}

data class PaymentOnExecution internal constructor(
    val id: PaymentId,
    val paymentList: List<Payment>
) : PaymentState() {
    override fun receive(event: PaymentEvent): PaymentState = when (event) {
        is PaymentExecutionStarted -> executePayment()
        is PaymentExecuted -> startRegistration()
        else -> this
    }
}

data class PaymentExecutionEnd internal constructor(
    val id: PaymentId
) : PaymentState() {
    override fun receive(event: PaymentEvent): PaymentState = when (event) {
        is PaymentEndRegisterStarted -> registerEnd()
        else -> this
    }

}

fun InitialState.register(id: PaymentId, paymentList: List<Payment>): PaymentState {
    println("##### Payment $id with transactions $paymentList registered")
    return PaymentOnExecution(id, paymentList)
}

fun PaymentOnExecution.executePayment(): PaymentState =
    when (val transaction = paymentList.firstOrNull { !it.alreadyExecuted }) {
        null -> PaymentOnExecution(id, paymentList)
        else -> {
            println("##### Executing transaction $transaction")
            PaymentOnExecution(id, paymentList).receive(PaymentExecuted(id))
        }
    }

fun PaymentOnExecution.startRegistration() =
    PaymentExecutionEnd(id).receive(PaymentEndRegisterStarted(id, Instant.now()))

fun PaymentExecutionEnd.registerEnd(): PaymentState {
    println("#### Payment $id ended")
    return PaymentExecutionEnd(id)
}