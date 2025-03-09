package com.danapple.openexchange.api

import com.danapple.openexchange.dao.CustomerDao
import com.danapple.openexchange.dao.OrderDao
import com.danapple.openexchange.dto.CancelReplace
import com.danapple.openexchange.dto.NewOrder
import com.danapple.openexchange.dto.Order
import com.danapple.openexchange.dto.OrderLeg
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
               @Autowired val customerDao: CustomerDao, @Autowired val orderDao: OrderDao) {

    @PostMapping("/{clientOrderId}")
    fun newOrder(@PathVariable("clientOrderId") clientOrderId: String, @RequestBody newOrder: NewOrder, @CookieValue(value = "customerId") customerId: String) : ResponseEntity<Order> {

        logger.info("newOrderSingle for customerId $customerId, clientOrderId $clientOrderId: $newOrder")

        val createdOrder = orderFactory.createOrder(customerId, clientOrderId, newOrder)

        val engine = engines[createdOrder.instrument]
            ?: return ResponseEntity(HttpStatus.FAILED_DEPENDENCY)
        val orderState = OrderState(createdOrder)
        logger.info("OrderState $orderState")

        orderDao.saveOrder(orderState)
        engine.newOrder(orderState)
        return createOrderResponse(orderState, HttpStatus.OK)

    }

    @PutMapping("/{clientOrderId}")
    fun cancelReplace(@PathVariable("clientOrderId") clientOrderId: String, @RequestBody cancelReplace: CancelReplace, @CookieValue(value = "customerId") customerId: String) : ResponseEntity<Order> {
        logger.info("cancelReplace for customerId $customerId, clientOrderId $clientOrderId: $cancelReplace")

        val originalOrderState = orderDao.getOrder(customerDao.getCustomer(customerId), cancelReplace.originalClientOrderId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        val engine = engines[originalOrderState.order.instrument]
            ?: return ResponseEntity(HttpStatus.FAILED_DEPENDENCY)

        val remainingQuantity = originalOrderState.remainingQuantity
        val orderStatus = originalOrderState.orderStatus


        var newQuantity = when(cancelReplace.capping) {
            CancelReplace.CAPPING.CAP_AT_REMAINING_QUANTITY -> min(remainingQuantity, cancelReplace.quantity)
            CancelReplace.CAPPING.UNCAPPED -> cancelReplace.quantity
        }

        newQuantity = when(cancelReplace.behavior) {
            CancelReplace.BEHAVIOR.ONLY_IF_OPEN -> if (originalOrderState.orderStatus == OrderStatus.OPEN) newQuantity else 0
            CancelReplace.BEHAVIOR.EVEN_IF_FILLED -> if (originalOrderState.orderStatus != OrderStatus.CANCELED) newQuantity else 0
        }

        if (newQuantity != 0) {
            val createdOrder = orderFactory.createOrder(customerId, clientOrderId, cancelReplace)

            if (originalOrderState.order.instrument !== createdOrder.instrument) {
                return createOrderResponse(originalOrderState, HttpStatus.BAD_REQUEST)
            }
            val orderState = OrderState(createdOrder)
            orderDao.saveOrder(orderState)
            engine.cancelReplace(originalOrderState, orderState)
            return createOrderResponse(orderState, HttpStatus.OK)
        } else {
            engine.cancelOrder(originalOrderState)
            return createOrderResponse(originalOrderState, HttpStatus.EXPECTATION_FAILED)
        }
    }

    @GetMapping("/{clientOrderId}")
    fun getOrder(@PathVariable("clientOrderId") clientOrderId: String, @CookieValue(value = "customerId") customerId: String) : ResponseEntity<Order> {
        logger.info("getOrder for customerId $customerId, clientOrderId $clientOrderId")
        val orderState = orderDao.getOrder(customerDao.getCustomer(customerId), clientOrderId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return createOrderResponse(orderState, HttpStatus.OK)

    }

    @DeleteMapping("{clientOrderId}")
    fun cancelOrder(@PathVariable clientOrderId: String, @CookieValue(value = "customerId") customerId: String) : ResponseEntity<Order> {
        logger.info("cancelOrder for customerId $customerId, clientOrderId $clientOrderId")
        val orderState = orderDao.getOrder(customerDao.getCustomer(customerId), clientOrderId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        val engine = engines[orderState.order.instrument]
            ?: return ResponseEntity(HttpStatus.FAILED_DEPENDENCY)
        if (orderState.orderStatus.viability == OrderStatus.Viability.ALIVE) {
            engine.cancelOrder(orderState)
            return createOrderResponse(orderState, HttpStatus.OK)
        } else {
            return createOrderResponse(orderState, HttpStatus.EXPECTATION_FAILED)

        }
    }

    private fun createOrderResponse(orderState: OrderState, httpStatus: HttpStatus): ResponseEntity<Order> {
        val returningOrder = Order(
            orderState.order.clientOrderId,
            orderState.order.timeStamp,
            orderState.orderStatus,
            orderState.order.price,
            orderState.order.quantity,
            orderState.remainingQuantity,
            listOf(OrderLeg(orderState.order.instrument.instrumentId, 1))
        )
        return ResponseEntity(returningOrder, httpStatus)
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(OrderApi::class.java)
    }
}