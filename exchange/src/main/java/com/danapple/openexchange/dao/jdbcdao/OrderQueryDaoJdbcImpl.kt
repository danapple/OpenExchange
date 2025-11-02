package com.danapple.openexchange.dao.jdbcdao

import com.danapple.openexchange.dao.CustomerDao
import com.danapple.openexchange.dao.InstrumentDao
import com.danapple.openexchange.dao.OrderQueryDao
import com.danapple.openexchange.entities.customers.Customer
import com.danapple.openexchange.orders.OrderState
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository("orderQueryDao")
@Transactional
open class OrderQueryDaoJdbcImpl(@Qualifier("orderJdbcClients") jdbcClients : List<JdbcClient>,
                                 private val customerDao : CustomerDao,
                                 private val instrumentDao : InstrumentDao,
                                 private val orderCache : OrderCache) : OrderQueryDao, ShardedDaoJdbcImpl(jdbcClients){

    override fun getOrder(customer: Customer, clientOrderId: String): OrderState? {
        val orderState = orderCache.getOrder(customer, clientOrderId)
            ?: TODO("Retrieving from DB not yet implemented")
        return orderState
    }

    override fun getOrders(customer: Customer): Collection<OrderState> {
        return orderCache.getOrders(customer)
    }

    override fun getOpenOrders(): Collection<OrderState> {
        val orders = ArrayList<OrderState>()
        val orderRowCallbackHandler = OrderRowCallbackHandler(orders, customerDao, instrumentDao)
        jdbcClients.forEach( { jdbcClient ->
            val statement = jdbcClient.sql(
                """SELECT base.orderId, base.customerId, base.createTime, base.clientOrderId, base.instrumentId, 
                        base.price, base.quantity, max(state.orderStatus), max(state.updateTime), max(state.versionNumber),
                        sum(coalesce(leg.quantity, 0)) filledQuantity
                    FROM order_base base
                    JOIN order_state state on state.orderId = base.orderId
                    LEFT OUTER JOIN trade_leg leg on leg.orderId = base.orderId
                    WHERE state.orderStatus = 'OPEN'
                    GROUP BY base.orderId
                    """);
            statement.query(orderRowCallbackHandler)
        })
        orders.forEach { orderState -> orderCache.addOrder(orderState) }
        return orders
    }
}