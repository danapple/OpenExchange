package com.danapple.openexchange.dao.jdbcdao

import com.danapple.openexchange.dao.CustomerDao
import com.danapple.openexchange.dao.InstrumentDao
import com.danapple.openexchange.dao.OrderQueryDao
import com.danapple.openexchange.entities.customers.Customer
import com.danapple.openexchange.orders.OrderState
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component("orderQueryDao")
@Transactional
open class OrderQueryDaoJdbcImpl(@Qualifier("orderJdbcClients") jdbcClients : List<JdbcClient>,
                                 private val customerDao : CustomerDao,
                                 private val instrumentDao : InstrumentDao,
                                 private val orderCache : OrderCache) : OrderQueryDao, ShardedDaoJdbcImpl(jdbcClients){

    override fun getOrder(customer: Customer, clientOrderId: String): OrderState? {
        val orderState = orderCache.getOrder(customer, clientOrderId)
        if (orderState == null) {
            TODO("Retrieving from DB not yet implemented and add to cache")
        }
        return orderState
    }

    override fun getOrders(customer: Customer): Collection<OrderState> {
        return orderCache.getOrders(customer)
    }

    override fun getOpenOrders(): Collection<OrderState> {
        val orders = ArrayList<OrderState>()
        val orderRowMapper = OrderRowMapper(orders, customerDao, instrumentDao)
        jdbcClients.forEach( { jdbcClient ->
            val statement = jdbcClient.sql(
                """SELECT ords.orderId, ords.customerId, ords.createtime, ords.clientOrderId, ords.instrumentId, ords.price, ords.quantity, states.orderStatus,
                    sum(ifNull(legs.quantity, 0)) filledQuantity
                    FROM orders ords
                    JOIN order_states states on states.orderId = ords.orderId
                    LEFT OUTER JOIN trade_legs legs on legs.orderId = ords.orderId
                    WHERE states.orderStatus = 'OPEN'
                    GROUP BY ords.orderId
                    """);
            statement.query(orderRowMapper)
        })
        orders.forEach { orderState -> orderCache.addOrder(orderState) }
        return orders
    }
}