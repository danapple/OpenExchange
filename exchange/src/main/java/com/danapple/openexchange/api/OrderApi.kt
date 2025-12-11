package com.danapple.openexchange.api

import com.danapple.openexchange.dao.CustomerDao
import com.danapple.openexchange.dao.OrderQueryDao
import com.danapple.openexchange.dto.CancelReplace
import com.danapple.openexchange.dto.OrderStates
import com.danapple.openexchange.dto.OrderStatus
import com.danapple.openexchange.engine.Engine
import com.danapple.openexchange.entities.instruments.Instrument
import com.danapple.openexchange.orders.OrderFactory
import com.danapple.openexchange.orders.OrderState
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.math.min

@RestController
@RequestMapping("/orders")
class OrderApi(private val engines : Map<Instrument, Engine>, private val orderFactory: OrderFactory,
               private val customerDao: CustomerDao, private val orderQueryDao: OrderQueryDao) : BaseApi() {

    @PutMapping("/{clientOrderId}")
    fun cancelReplace(@PathVariable("clientOrderId") clientOrderId: String, @RequestBody cancelReplace: CancelReplace):
            ResponseEntity<OrderStates> {
        val customer = getCustomer()
        logger.debug("cancelReplace for customerId ${customer.customerId}, clientOrderId $clientOrderId: $cancelReplace")
        val originalOrderState = orderQueryDao.getOrder(customer, cancelReplace.originalClientOrderId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        val engine = engines[originalOrderState.order.instrument]
            ?: return ResponseEntity(HttpStatus.FAILED_DEPENDENCY)

        // TODO All of this logic needs to be @Synchronized in the Engine
        val remainingQuantity = originalOrderState.remainingQuantity
        val originalOrderStatus = originalOrderState.orderStatus

        var newQuantity = when(cancelReplace.capping) {
            CancelReplace.CAPPING.CAP_AT_REMAINING_QUANTITY -> min(remainingQuantity, cancelReplace.order.quantity)
            CancelReplace.CAPPING.UNCAPPED -> cancelReplace.order.quantity
        }

        newQuantity = when(cancelReplace.behavior) {
            CancelReplace.BEHAVIOR.ONLY_IF_OPEN -> if (originalOrderStatus == OrderStatus.OPEN) newQuantity else 0
            CancelReplace.BEHAVIOR.EVEN_IF_FILLED -> if (originalOrderStatus == OrderStatus.OPEN || originalOrderStatus == OrderStatus.FILLED) newQuantity else 0
        }

        if (newQuantity != 0) {
            val createdOrder = orderFactory.createOrder(customer, clientOrderId, cancelReplace)

            if (originalOrderState.order.instrument !== createdOrder.instrument) {
                return createOrderStatesResponse(orderStates = arrayOf(originalOrderState), HttpStatus.BAD_REQUEST)
            }
            val orderState = OrderState(createdOrder, createdOrder.createTime, OrderStatus.OPEN)

            engine.cancelReplace(originalOrderState, orderState)
            return createOrderStatesResponse(orderStates = arrayOf(orderState, originalOrderState), HttpStatus.OK)
        } else {
            engine.cancelOrder(originalOrderState)
            return createOrderStatesResponse(orderStates = arrayOf(originalOrderState), HttpStatus.EXPECTATION_FAILED)
        }
    }

    @GetMapping("/{clientOrderId}")
    fun getOrder(@PathVariable("clientOrderId") clientOrderId: String) : ResponseEntity<OrderStates> {
        val customer = getCustomer()
        logger.debug("getOrder for customerId ${customer.customerId}, clientOrderId $clientOrderId")

        val orderState = orderQueryDao.getOrder(customer, clientOrderId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        return createOrderStatesResponse(orderStates = arrayOf(orderState), HttpStatus.OK)
    }

    @DeleteMapping("/{clientOrderId}")
    fun cancelOrder(@PathVariable("clientOrderId") clientOrderId: String) : ResponseEntity<OrderStates> {
        val customer = getCustomer()
        logger.debug("cancelOrder for customerId ${customer.customerId}, clientOrderId $clientOrderId")
        val orderState = orderQueryDao.getOrder(customer, clientOrderId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        logger.trace("Got orderState with instrument {}", orderState.order.instrument.instrumentId)
        val engine = engines[orderState.order.instrument]?: return ResponseEntity(HttpStatus.FAILED_DEPENDENCY)
        logger.trace("Got engine")

        engine.cancelOrder(orderState)
        return createOrderStatesResponse(orderStates = arrayOf(orderState), HttpStatus.OK)

    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(OrderApi::class.java)
    }
}