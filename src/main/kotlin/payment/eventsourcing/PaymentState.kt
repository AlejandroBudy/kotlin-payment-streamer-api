package payment.eventsourcing

import payment.command.Context
import payment.domain.Payment
import java.time.Instant

sealed class PaymentState : EntityState<PaymentEvent> {
    abstract override infix fun receive(event: PaymentEvent): PaymentState
}

class InitialState(val context: Context) : PaymentState() {
    override fun receive(event: PaymentEvent): PaymentState = when (event) {
        is PaymentInitiated -> register(event.id, event.paymentList)
        else -> this
    }
}

data class PaymentOnExecution internal constructor(
    val context: Context,
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
    val context: Context,
    val id: PaymentId
) : PaymentState() {
    override fun receive(event: PaymentEvent): PaymentState = when (event) {
        is PaymentEndRegisterStarted -> registerEnd()
        else -> this
    }
}

fun InitialState.register(id: PaymentId, paymentList: List<Payment>): PaymentState {
    context.persistenceRepository(paymentList).also {
        println("##### Payment $id with transactions $paymentList registered")
    }
    return PaymentOnExecution(context, id, paymentList) receive PaymentExecutionStarted(id, paymentList)
}

fun PaymentOnExecution.executePayment(): PaymentState =
    when (val transaction = paymentList.firstOrNull { !it.alreadyExecuted }) {
        null -> PaymentOnExecution(context, id, paymentList).receive(PaymentExecuted(id))
        else -> {
            println("##### Executing transaction $transaction")
            context.executorRepository(transaction)
            // update payment list
            PaymentOnExecution(context, id, paymentList) receive PaymentExecutionStarted(id, paymentList)
        }
    }

fun PaymentOnExecution.startRegistration() =
    PaymentExecutionEnd(context, id) receive PaymentEndRegisterStarted(id, Instant.now())

fun PaymentExecutionEnd.registerEnd(): PaymentState {
    println("#### Payment $id ended")
    return PaymentExecutionEnd(context, id)
}