package com.danapple.openexchange.api

import com.danapple.openexchange.dao.CustomerDao
import com.danapple.openexchange.dao.OrderQueryDao
import com.danapple.openexchange.dto.*
import com.danapple.openexchange.engine.Engine
import com.danapple.openexchange.entities.instruments.Instrument
import com.danapple.openexchange.entities.orders.toDto
import com.danapple.openexchange.orders.OrderFactory
import com.danapple.openexchange.orders.OrderState
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class OrdersApi(private val engines : Map<Instrument, Engine>, private val orderFactory: OrderFactory,
                private val customerDao: CustomerDao, private val orderQueryDao: OrderQueryDao) : BaseApi() {

    @PostMapping
    fun submitOrders(@RequestBody submitOrders: SubmitOrders) : ResponseEntity<OrderStates> {
        val orderStates = submitOrders.orders.map { submittedOrder ->
            try {
                val customer = getCustomer()
                if (logger.isDebugEnabled) {
                    logger.debug("New Order for customerId ${customer.customerId}, clientOrderId ${submittedOrder.clientOrderId}: $submitOrders")
                }
                val createdOrder = orderFactory.createOrder(customer, submittedOrder.clientOrderId, submittedOrder)
                val engine = engines[createdOrder.instrument]
                val orderState =
                    OrderState(createdOrder, if (engine == null) OrderStatus.REJECTED else OrderStatus.OPEN)
                if (logger.isDebugEnabled) {
                    logger.debug("OrderState $orderState")
                }
                engine?.newOrder(orderState)
                orderState.toDto()
            }
            catch (e : Exception) {
                logger.warn("Unable to handle new order $submittedOrder", e)
                OrderState(0, OrderStatus.REJECTED, 0, submittedOrder)
            }
        }.toTypedArray()
        return createOrderStatesResponse(orderStates = orderStates, HttpStatus.OK)
    }

    @GetMapping
    fun getOrders() : ResponseEntity<OrderStates> {
        val customer = getCustomer()
        if (logger.isDebugEnabled) {
            logger.debug("getOrders for customerId ${customer.customerId}")
        }
        val orderStates = orderQueryDao.getOrders(customer)
        return createOrderStatesResponse(orderStates = orderStates.toTypedArray(), HttpStatus.OK)
    }

    @DeleteMapping
    fun cancelOrders() : ResponseEntity<OrderStates> {
        val customer = getCustomer()
        if (logger.isDebugEnabled) {
            logger.debug("cancelOrders for customerId ${customer.customerId}")
        }
        val orderStatesToCancel = orderQueryDao.getOrders(customer)
        val resultingOrderStates = orderStatesToCancel.map{ orderState ->
            try {
                engines[orderState.order.instrument]?.cancelOrder(orderState)
            } catch (e: Exception) {
                logger.warn("Unable to cancel order $orderState", e);
            }
            orderState.toDto()
        }.toTypedArray()
        return createOrderStatesResponse(orderStates = resultingOrderStates, HttpStatus.OK)
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(OrdersApi::class.java)
    }
}