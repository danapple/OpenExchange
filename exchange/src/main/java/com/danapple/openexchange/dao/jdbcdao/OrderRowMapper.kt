package com.danapple.openexchange.dao.jdbcdao

import com.danapple.openexchange.dao.CustomerDao
import com.danapple.openexchange.dao.InstrumentDao
import com.danapple.openexchange.dto.OrderStatus
import com.danapple.openexchange.orders.Order
import com.danapple.openexchange.orders.OrderState
import org.springframework.jdbc.core.RowCallbackHandler
import java.sql.ResultSet

class OrderRowMapper(private val orderStates : MutableList<OrderState>, private val customerDao : CustomerDao, private val instrumentDao : InstrumentDao) : RowCallbackHandler {
    var count : Int = 0
    override fun processRow(rs: ResultSet) {
        val order = Order(rs.getLong("orderId"),
            customerDao.getCustomer(rs.getLong("customerId")) ?: throw RuntimeException("No customer"),
            rs.getLong("createtime"),
            rs.getString("clientOrderId"),
            instrumentDao.getInstrument(rs.getLong("instrumentId")) ?: throw RuntimeException("No instrument"),
            rs.getBigDecimal("price"),
            rs.getInt("quantity"))
        count++
        orderStates.add(OrderState(order, OrderStatus.valueOf(rs.getString("orderStatus")), filledQty = rs.getInt("filledQuantity")))
    }
}
