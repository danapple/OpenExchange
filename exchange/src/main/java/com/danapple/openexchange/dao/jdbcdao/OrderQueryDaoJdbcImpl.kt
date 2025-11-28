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
                                 private val orderCache : OrderCache,
                                 @Qualifier("orderDatabaseConfiguration") databaseConfiguration : DatabaseConfiguration) : OrderQueryDao, ShardedDaoJdbcImpl(
    jdbcClients,
    databaseConfiguration.shardCount
) {

    // Shared SQL structure
    private fun generateOrderQuery(filter: String): String {
        return """
            SELECT base.orderId, base.customerId, base.clientOrderId, base.createTime, 
                   base.instrumentId, base.price, base.quantity,
                   max(state.orderStatus) orderStatus, max(state.updateTime) updateTime,
                   max(state.versionNumber) versionNumber, sum(coalesce(leg.quantity, 0)) filledQuantity
            FROM order_base base
            JOIN order_state state ON state.orderId = base.orderId
            LEFT OUTER JOIN trade_leg leg ON leg.orderId = base.orderId
            $filter
            GROUP BY base.orderId
        """.trimIndent()
    }

    override fun getOrder(customer: Customer, clientOrderId: String): OrderState? {
        // Check if the order exists in the cache
        val cachedOrder = orderCache.getOrder(customer, clientOrderId)
        if (cachedOrder != null) {
            return cachedOrder
        }

        // Placeholder to store the found order
        var foundOrder: OrderState? = null

        // SQL filter specific to getOrder
        val filter = "WHERE base.customerId = ? AND base.clientOrderId = ?"
        val sql = generateOrderQuery(filter)

        // Iterate through all JdbcClients (shards) to find the order
        for (jdbcClient in jdbcClients) {

            // Create a list to temporarily store the result
            val orders = ArrayList<OrderState>()

            // Use OrderRowCallbackHandler to process the ResultSet
            val orderRowCallbackHandler = OrderRowCallbackHandler(orders, customerDao, instrumentDao)
            jdbcClient.sql(sql).param(customer.customerId).param(clientOrderId).query(orderRowCallbackHandler)

            // Check if any orders were found, then stop the iteration
            if (orders.isNotEmpty()) {
                foundOrder = orders[0]
                break // Exit the loop once the order is found
            }
        }

        // Cache the found order (optional)
        foundOrder?.let { orderCache.addOrder(it) }

        // Return the found order or null if no order exists
        return foundOrder
    }

    override fun getOrders(customer: Customer): Collection<OrderState> {
        return orderCache.getOrders(customer)
    }

    override fun getOpenOrders(): Collection<OrderState> {
        // SQL filter specific to getOpenOrders
        val filter = "WHERE state.orderStatus = 'OPEN'"

        // Placeholder for resulting orders
        val orders = ArrayList<OrderState>()
        val sql = generateOrderQuery(filter)

        // Iterate through all shards (jdbcClients)
        val orderRowCallbackHandler = OrderRowCallbackHandler(orders, customerDao, instrumentDao)
        jdbcClients.forEach { jdbcClient ->
            jdbcClient.sql(sql).query(orderRowCallbackHandler)
        }

        // Cache the orders (optional)
        orders.forEach { orderState -> orderCache.addOrder(orderState) }

        return orders
    }
}