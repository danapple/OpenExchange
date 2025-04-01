package com.danapple.openexchange.dao.jdbcdao

import com.danapple.openexchange.dao.OrderDao
import com.danapple.openexchange.orders.OrderState
import jakarta.persistence.OptimisticLockException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository("orderDao")
@Transactional
open class OrderDaoJdbcImpl(@Qualifier("orderJdbcClients") jdbcClients : List<JdbcClient>, private val orderCache : OrderCache) : OrderDao, ShardedDaoJdbcImpl(jdbcClients) {

    override fun saveOrder(orderState: OrderState) {
        val jdbcClient = getJdbcClient(orderState.order.instrument.instrumentId)
        val orderStatement = jdbcClient.sql(
            """INSERT INTO orders (orderId, customerId, createTime, clientOrderId, instrumentId, price, quantity) 
                VALUES (:orderId, :customerId, :createTime, :clientOrderId, :instrumentId, :price, :quantity)""")
            .param("orderId", orderState.order.orderId)
            .param("customerId", orderState.order.customer.customerId)
            .param("createTime", orderState.order.createTime)
            .param("clientOrderId", orderState.order.clientOrderId)
            .param("instrumentId", orderState.order.instrument.instrumentId)
            .param("price", orderState.order.price)
            .param("quantity", orderState.order.quantity)
        val update = orderStatement.update()

        if (update == 1) {
            val orderStateStatement = jdbcClient.sql(
                """INSERT INTO order_states (orderId, orderStatus, versionNumber) 
                    VALUES (:orderId, :orderStatus, :versionNumber)""")
                .param("orderId", orderState.order.orderId)
                .param("orderStatus", orderState.orderStatus.toString())
                .param("versionNumber", orderState.versionNumber)
            orderStateStatement.update()
        }
        orderCache.addOrder(orderState)
    }

    override fun updateOrder(orderState: OrderState) {
        val jdbcClient = getJdbcClient(orderState.order.instrument.instrumentId)

        val orderStateStatement = jdbcClient.sql(
                """UPDATE order_states
                    set orderStatus = :orderStatus,
                        versionNumber = :versionNumber + 1
                    where orderId = :orderId
                        and versionNumber = :versionNumber""")
                .param("orderId", orderState.order.orderId)
                .param("orderStatus", orderState.orderStatus.toString())
                .param("versionNumber", orderState.versionNumber)
        val rowUpdateCount = orderStateStatement.update()
        if (rowUpdateCount != 1) {
            throw OptimisticLockException("Optimistic locking exception on OrderState ${orderState.order.orderId}, rowUpdateCount = $rowUpdateCount")
        }
        orderState.versionNumber++

    }
}