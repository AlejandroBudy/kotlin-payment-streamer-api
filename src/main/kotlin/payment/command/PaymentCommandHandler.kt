package payment.command

import payment.define.Has
import payment.domain.PaymentRetriever
import payment.eventsourcing.InitialState
import payment.eventsourcing.PaymentEvent
import payment.eventsourcing.PaymentId
import payment.eventsourcing.PaymentInitiated

typealias CommandHandler = (PaymentCommand) -> List<PaymentEvent>

interface Context : Has.PersistenceRepository, Has.ExecutorRepository

class PaymentCommandHandler(
    private val entityRetriever: PaymentRetriever,
    private val context: Context
) : CommandHandler {

    override fun invoke(command: PaymentCommand): List<PaymentEvent> = when (command) {
        is InitPaymentCommand -> command.execute()
    }

    private fun InitPaymentCommand.execute(): List<PaymentEvent> =
        when (entityRetriever.retrieveById(PaymentId(payment.id)) ?: InitialState(context)) {
            is InitialState -> PaymentInitiated(PaymentId(payment.id), listOf(payment)).toList()
            else -> throw IllegalStateException("Command cant be handle for current state")
        }


    private fun PaymentEvent.toList(): List<PaymentEvent> = listOf(this)
}

