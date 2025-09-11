package com.danapple.openexchange.entities.trades

fun TradeLeg.toExecution() : com.danapple.openexchange.dto.Execution {
    return com.danapple.openexchange.dto.Execution(
        orderState.order.clientOrderId,
        trade.createTime,
        trade.price,
        quantity)
}
