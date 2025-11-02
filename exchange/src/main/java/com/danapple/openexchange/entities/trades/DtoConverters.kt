package com.danapple.openexchange.entities.trades

fun TradeLeg.toExecution() : com.danapple.openexchange.dto.Execution {
    return com.danapple.openexchange.dto.Execution(
        orderState.order.clientOrderId,
        orderState.order.instrument.instrumentId,
        trade.createTime,
        trade.price,
        quantity)
}
