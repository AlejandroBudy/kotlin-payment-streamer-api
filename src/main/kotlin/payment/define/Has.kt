package payment.define

import payment.domain.PaymentExecutor
import payment.domain.PaymentPersister

interface Has {
    interface PersistenceRepository {
        val persistenceRepository: PaymentPersister
    }
    interface ExecutorRepository {
        val executorRepository: PaymentExecutor
    }
}