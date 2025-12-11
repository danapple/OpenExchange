package com.danapple.openexchange.customerupdates

import com.danapple.openexchange.dto.ExecutionsTopicWrapper
import com.danapple.openexchange.entities.orders.toDto
import com.danapple.openexchange.entities.trades.Trade
import com.danapple.openexchange.entities.trades.toExecution
import com.danapple.openexchange.orders.OrderState
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class CustomerUpdateSender(private val simpMessagingTemplate: SimpMessagingTemplate) {
    fun sendTrade(trade: Trade) {
        trade.tradeLegs.forEach { tradeLeg ->
            simpMessagingTemplate.convertAndSendToUser(
                tradeLeg.orderState.order.customer.customerId.toString(),
                "/queue/executions",
                ExecutionsTopicWrapper(tradeLeg.toExecution())
            )
            sendOrderState(tradeLeg.orderState)
        }
    }

    fun sendOrderState(orderState: OrderState) {
        simpMessagingTemplate.convertAndSendToUser(
            orderState.order.customer.customerId.toString(),
            "/queue/executions",
            ExecutionsTopicWrapper(orderState.toDto())
        )
    }
}