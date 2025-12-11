package com.danapple.openexchange.entities.orders

import com.danapple.openexchange.dto.OrderLeg
import com.danapple.openexchange.orders.Order
import com.danapple.openexchange.orders.OrderState

fun Order.toDto(): com.danapple.openexchange.dto.Order {
    return com.danapple.openexchange.dto.Order(
        clientOrderId,
        price,
        quantity,
        listOf(OrderLeg(instrument.instrumentId, 1))
    )
}

fun OrderState.toDto(): com.danapple.openexchange.dto.OrderState {
    return com.danapple.openexchange.dto.OrderState(
        order.createTime,
        orderStatus,
        remainingQuantity,
        versionNumber,
        order.toDto()
    )
}
