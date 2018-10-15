package com.drestaurant.customer.domain

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateNotFoundException
import org.axonframework.commandhandling.model.Repository
import org.axonframework.eventhandling.EventBus
import org.axonframework.eventhandling.GenericEventMessage.asEventMessage

internal open class CustomerCommandHandler(private val repository: Repository<Customer>, private val eventBus: EventBus) {

    @CommandHandler
    fun handle(command: ValidateOrderByCustomerInternalCommand) = try {
        repository.load(command.customerId).execute { it.validateOrder(command.orderId, command.orderTotal, command.auditEntry) }
    } catch (exception: AggregateNotFoundException) {
        eventBus.publish(asEventMessage<Any>(CustomerNotFoundForOrderInternalEvent(command.customerId, command.orderId, command.orderTotal, command.auditEntry)))
    }
}
