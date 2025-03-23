package com.danapple.openexchange.dao.jdbcdao

import com.danapple.openexchange.dao.OrderDao
import com.danapple.openexchange.orders.OrderState
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.atomic.AtomicLong

@Component("orderDao")
@Transactional
open class OrderDaoJdbcImpl(@Qualifier("orderJdbcClients") jdbcClients : List<JdbcClient>, private val orderCache : OrderCache) : OrderDao, ShardedDaoJdbcImpl(jdbcClients) {

    private var orderId = AtomicLong()

    override fun saveOrder(orderState: OrderState) {
        val newOrderId = orderId.getAndIncrement()
        val jdbcClient = getJdbcClient(orderState.order.instrument.instrumentId)
        val orderStatement = jdbcClient.sql(
            """INSERT INTO orders (orderId, customerId, createTime, clientOrderId, instrumentId, price, quantity) 
                VALUES (:orderId, :customerId, :createTime, :clientOrderId, :instrumentId, :price, :quantity)
            """)
            .param("orderId", newOrderId)
            .param("customerId", orderState.order.customer.customerId)
            .param("createTime", orderState.order.createTime)
            .param("clientOrderId", orderState.order.clientOrderId)
            .param("instrumentId", orderState.order.instrument.instrumentId)
            .param("price", orderState.order.price)
            .param("quantity", orderState.order.quantity)
        val update = orderStatement.update()

        if (update == 1) {
            val orderStateStatement = jdbcClient.sql(
                """INSERT INTO order_states (orderId, orderStatus) 
                    VALUES (:orderId, :orderStatus)
                """)
                .param("orderId", newOrderId)
                .param("orderStatus", orderState.orderStatus.toString())
            orderStateStatement.update()
        }
        orderCache.addOrder(orderState)
    }

    override fun updateOrder(orderState: OrderState) {
        val jdbcClient = getJdbcClient(orderState.order.instrument.instrumentId)

        val orderStateStatement = jdbcClient.sql(
                """UPDATE order_states
                    set orderStatus = :orderStatus
                    where orderId = :orderId
                """)
                .param("orderId", orderState.order.orderId)
                .param("orderStatus", orderState.orderStatus.toString())
            orderStateStatement.update()

    }
}