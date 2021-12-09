package payment.eventsourcing

import payment.domain.Payment
import java.time.Instant

typealias PaymentId = EntityId

sealed class PaymentEvent : EntityEvent
data class PaymentInitiated(override val id: PaymentId, val paymentList: List<Payment>) : PaymentEvent()
data class PaymentExecutionStarted(override val id: PaymentId, val paymentList: List<Payment>) : PaymentEvent()
data class PaymentExecuted(override val id: PaymentId) : PaymentEvent()
data class PaymentEndRegisterStarted(override val id: PaymentId, val valueDate: Instant) : PaymentEvent()