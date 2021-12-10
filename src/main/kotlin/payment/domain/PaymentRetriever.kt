package payment.domain

import payment.eventsourcing.EntityRetriever
import payment.eventsourcing.PaymentEvent
import payment.eventsourcing.PaymentId
import payment.eventsourcing.PaymentState

interface PaymentRetriever : EntityRetriever<PaymentState, PaymentEvent> {
    override fun retrieveById(id: PaymentId): PaymentState?
}