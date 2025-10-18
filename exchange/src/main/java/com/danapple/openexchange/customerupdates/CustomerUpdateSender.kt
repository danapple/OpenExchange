package com.danapple.openexchange.customerupdates

import com.danapple.openexchange.entities.orders.toDto
import com.danapple.openexchange.entities.trades.Trade
import com.danapple.openexchange.entities.trades.toExecution
import com.danapple.openexchange.orders.OrderState
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class CustomerUpdateSender(private val simpMessagingTemplate: SimpMessagingTemplate) {
    fun sendTrade(trade : Trade) {
        trade.tradeLegs.forEach { tradeLeg ->
            simpMessagingTemplate.convertAndSend(
                "/topics/executions/%s".format(tradeLeg.orderState.order.customer.customerKey),
                tradeLeg.toExecution())
        }
    }

    fun sendOrderState(orderState : OrderState) {
        simpMessagingTemplate.convertAndSend(
            "/topics/executions/%s".format(orderState.order.customer.customerKey),
            orderState.toDto())
    }
}