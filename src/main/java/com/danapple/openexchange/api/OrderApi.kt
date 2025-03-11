package com.danapple.openexchange.api

import com.danapple.openexchange.dao.CustomerDao
import com.danapple.openexchange.dao.OrderDao
import com.danapple.openexchange.dto.*
import com.danapple.openexchange.engine.Engine
import com.danapple.openexchange.instruments.Instrument
import com.danapple.openexchange.orders.OrderFactory
import com.danapple.openexchange.orders.OrderState
import com.danapple.openexchange.orders.OrderStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.math.min

@RestController
@RequestMapping("/order")
class OrderApi(@Autowired val engines : Map<Instrument, Engine>, @Autowired val orderFactory: OrderFactory,
               @Autowired val customerDao: CustomerDao, @Autowired val orderDao: OrderDao) : BaseApi() {

    @PutMapping("/{clientOrderId}")
    fun cancelReplace(@PathVariable("clientOrderId") clientOrderId: String, @RequestBody cancelReplace: CancelReplace, @CookieValue(value = "customerId") customerId: String) : ResponseEntity<OrderStates> {
        logger.info("cancelReplace for customerId $customerId, clientOrderId $clientOrderId: $cancelReplace")

        val originalOrderState = orderDao.getOrder(customerDao.getCustomer(customerId), cancelReplace.originalClientOrderId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        val engine = engines[originalOrderState.order.instrument]
            ?: return ResponseEntity(HttpStatus.FAILED_DEPENDENCY)

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
            val createdOrder = orderFactory.createOrder(customerId, clientOrderId, cancelReplace)

            if (originalOrderState.order.instrument !== createdOrder.instrument) {
                return createOrderStatesResponse(orderStates = arrayOf(originalOrderState), HttpStatus.BAD_REQUEST)
            }
            val orderState = OrderState(createdOrder, OrderStatus.OPEN)

            orderDao.saveOrder(orderState)
            engine.cancelReplace(originalOrderState, orderState)
            return createOrderStatesResponse(orderStates = arrayOf(orderState, originalOrderState), HttpStatus.OK)
        } else {
            engine.cancelOrder(originalOrderState)
            return createOrderStatesResponse(orderStates = arrayOf(originalOrderState), HttpStatus.EXPECTATION_FAILED)
        }
    }

    @GetMapping("/{clientOrderId}")
    fun getOrder(@PathVariable("clientOrderId") clientOrderId: String, @CookieValue(value = "customerId") customerId: String) : ResponseEntity<OrderStates> {
        logger.info("getOrder for customerId $customerId, clientOrderId $clientOrderId")
        val orderState = orderDao.getOrder(customerDao.getCustomer(customerId), clientOrderId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return createOrderStatesResponse(orderStates = arrayOf(orderState), HttpStatus.OK)
    }

    @DeleteMapping("{clientOrderId}")
    fun cancelOrder(@PathVariable clientOrderId: String, @CookieValue(value = "customerId") customerId: String) : ResponseEntity<OrderStates> {
        logger.info("cancelOrder for customerId $customerId, clientOrderId $clientOrderId")
        val orderState = orderDao.getOrder(customerDao.getCustomer(customerId), clientOrderId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        val engine = engines[orderState.order.instrument]
            ?: return ResponseEntity(HttpStatus.FAILED_DEPENDENCY)
        if (orderState.orderStatus.viability == OrderStatus.Viability.ALIVE) {
            engine.cancelOrder(orderState)
            return createOrderStatesResponse(orderStates = arrayOf(orderState), HttpStatus.OK)
        } else {
            return createOrderStatesResponse(orderStates = arrayOf(orderState), HttpStatus.EXPECTATION_FAILED)
        }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(OrderApi::class.java)
    }
}