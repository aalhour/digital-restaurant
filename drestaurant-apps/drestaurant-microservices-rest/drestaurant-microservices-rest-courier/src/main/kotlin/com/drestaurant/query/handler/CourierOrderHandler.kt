package com.drestaurant.query.handler

import com.drestaurant.courier.domain.api.CourierOrderAssignedEvent
import com.drestaurant.courier.domain.api.CourierOrderCreatedEvent
import com.drestaurant.courier.domain.api.CourierOrderDeliveredEvent
import com.drestaurant.courier.domain.api.CourierOrderNotAssignedEvent
import com.drestaurant.courier.domain.model.CourierOrderState
import com.drestaurant.query.FindAllCourierOrdersQuery
import com.drestaurant.query.FindCourierOrderQuery
import com.drestaurant.query.model.CourierOrderEntity
import com.drestaurant.query.repository.CourierOrderRepository
import com.drestaurant.query.repository.CourierRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.AllowReplay
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventsourcing.SequenceNumber
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.stereotype.Component
import java.lang.UnsupportedOperationException

@Component
@ProcessingGroup("courier")
internal class CourierOrderHandler(private val repository: CourierOrderRepository, private val courierRepository: CourierRepository, private val queryUpdateEmitter: QueryUpdateEmitter) {

    @EventHandler
    @AllowReplay(false)
    fun handle(event: CourierOrderCreatedEvent, @SequenceNumber aggregateVersion: Long) {
        repository.save(CourierOrderEntity(event.aggregateIdentifier, aggregateVersion, null, CourierOrderState.CREATED))
    }

    @EventHandler
    @AllowReplay(false)
    fun handle(event: CourierOrderAssignedEvent, @SequenceNumber aggregateVersion: Long) {
        val courierEntity = courierRepository.findById(event.courierId).orElseThrow { UnsupportedOperationException("Courier with id '${event.courierId}' not found") }
        val record = repository.findById(event.aggregateIdentifier).orElseThrow { UnsupportedOperationException("Courier order with id '${event.aggregateIdentifier}' not found") }
        record.state = CourierOrderState.ASSIGNED
        record.courier = courierEntity
        repository.save(record)

        /* sending it to subscription queries of type FindCourierOrderQuery, but only if the courier order id matches. */
        queryUpdateEmitter.emit(
                FindCourierOrderQuery::class.java,
                { query -> query.courierOrderId.equals(event.aggregateIdentifier) },
                record
        )

        /* sending it to subscription queries of type FindAllCourierOrders. */
        queryUpdateEmitter.emit(
                FindAllCourierOrdersQuery::class.java,
                { true },
                record
        )
    }

    @EventHandler
    @AllowReplay(false)
    fun handle(event: CourierOrderNotAssignedEvent, @SequenceNumber aggregateVersion: Long) {
        val record = repository.findById(event.aggregateIdentifier).orElseThrow { UnsupportedOperationException("Courier order with id '${event.aggregateIdentifier}' not found") }

        /* sending it to subscription queries of type FindCourierOrderQuery, but only if the courier order id matches. */
        queryUpdateEmitter.emit(
                FindCourierOrderQuery::class.java,
                { query -> query.courierOrderId.equals(event.aggregateIdentifier) },
                record
        )

        /* sending it to subscription queries of type FindAllCourierOrders. */
        queryUpdateEmitter.emit(
                FindAllCourierOrdersQuery::class.java,
                { true },
                record
        )
    }

    @EventHandler
    @AllowReplay(false)
    fun handle(event: CourierOrderDeliveredEvent, @SequenceNumber aggregateVersion: Long) {
        val record = repository.findById(event.aggregateIdentifier).orElseThrow { UnsupportedOperationException("Courier order with id '${event.aggregateIdentifier}' not found") }
        record.state = CourierOrderState.DELIVERED
        repository.save(record)

        /* sending it to subscription queries of type FindCourierOrderQuery, but only if the courier order id matches. */
        queryUpdateEmitter.emit(
                FindCourierOrderQuery::class.java,
                { query -> query.courierOrderId.equals(event.aggregateIdentifier) },
                record
        )

        /* sending it to subscription queries of type FindAllCourierOrders. */
        queryUpdateEmitter.emit(
                FindAllCourierOrdersQuery::class.java,
                { true },
                record
        )
    }

    @QueryHandler
    fun handle(query: FindCourierOrderQuery): CourierOrderEntity = repository.findById(query.courierOrderId).orElseThrow { UnsupportedOperationException("Courier order with id '${query.courierOrderId}' not found") }

    @QueryHandler
    fun handle(query: FindAllCourierOrdersQuery): MutableIterable<CourierOrderEntity> = repository.findAll()
}
