package com.danapple.openexchange.dao.jdbcdao

import com.danapple.openexchange.dao.IdGenerator
import com.danapple.openexchange.dao.OrderDao
import com.danapple.openexchange.orders.OrderState
import jakarta.persistence.OptimisticLockException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Clock

@Repository("orderDao")
@Transactional
open class OrderDaoJdbcImpl(@Qualifier("orderJdbcClients") jdbcClients : List<JdbcClient>,
                            private val orderCache : OrderCache, private val clock: Clock,
                            @Qualifier("orderStateHistoryIdGenerator") private val orderStateHistoryIdGenerator: IdGenerator,
                            @Qualifier("orderDatabaseConfiguration") databaseConfiguration : DatabaseConfiguration
) : OrderDao, ShardedDaoJdbcImpl(jdbcClients, databaseConfiguration.shardCount) {

    override fun saveOrder(orderState: OrderState) {
        val jdbcClient = getJdbcClient(orderState.order.instrument.instrumentId)
        val orderStatement = jdbcClient.sql(
            """INSERT INTO order_base (orderId, customerId, createTime, clientOrderId, instrumentId, price, quantity) 
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
                """INSERT INTO order_state (orderId, orderStatus, updateTime, versionNumber) 
                    VALUES (:orderId, :orderStatus, :updateTime, :versionNumber)""")
                .param("orderId", orderState.order.orderId)
                .param("orderStatus", orderState.orderStatus.toString())
                .param("versionNumber", orderState.versionNumber)
                .param("updateTime", orderState.order.createTime)
            orderStateStatement.update()
            insertOrderStateHistory(jdbcClient, orderState, orderState.order.createTime)
        }
        orderCache.addOrder(orderState)
    }

    override fun updateOrder(orderState: OrderState) {
        val jdbcClient = getJdbcClient(orderState.order.instrument.instrumentId)
        val now = clock.millis()
        val orderStateStatement = jdbcClient.sql(
                """UPDATE order_state
                    set orderStatus = :orderStatus,
                        updateTime = :updateTime,
                        versionNumber = :versionNumber + 1
                    where orderId = :orderId
                        and versionNumber = :versionNumber""")
                .param("orderId", orderState.order.orderId)
                .param("orderStatus", orderState.orderStatus.toString())
                .param("versionNumber", orderState.versionNumber)
                .param("updateTime", now)

        val rowUpdateCount = orderStateStatement.update()
        if (rowUpdateCount != 1) {
            throw OptimisticLockException("Optimistic locking exception on OrderState ${orderState.order.orderId}, rowUpdateCount = $rowUpdateCount")
        }
        insertOrderStateHistory(jdbcClient, orderState, now)
        orderState.versionNumber++

    }

    private fun insertOrderStateHistory(jdbcClient: JdbcClient, orderState: OrderState, now: Long) {
        val orderStateHistoryStatement = jdbcClient.sql(
            """INSERT INTO order_state_history (orderStateHistoryId, orderId, orderStatus, createTime, versionNumber) 
                        VALUES (:orderStateHistoryId, :orderId, :orderStatus, :createTime, :versionNumber)"""
        )
            .param("orderStateHistoryId", orderStateHistoryIdGenerator.getNextId())
            .param("orderId", orderState.order.orderId)
            .param("orderStatus", orderState.orderStatus.toString())
            .param("versionNumber", orderState.versionNumber)
            .param("createTime", now)
        orderStateHistoryStatement.update()
    }
}