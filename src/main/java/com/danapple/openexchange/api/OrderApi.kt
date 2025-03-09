package com.danapple.openexchange.api

import com.danapple.openexchange.dto.CancelReplace
import com.danapple.openexchange.dto.NewOrder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/order")
class OrderApi() {

    @PostMapping("/{clientOrderId}")
    fun newOrder(@PathVariable("clientOrderId") clientOrderId: String, @RequestBody newOrder: NewOrder, @CookieValue(value = "customerId") customerId: String) : ResponseEntity<NewOrder> {

        logger.info("newOrderSingle for customerId $customerId, clientOrderId $clientOrderId: $newOrder")

        return ResponseEntity(HttpStatus.CREATED)
    }

    @PutMapping("/{clientOrderId}")
    fun cancelReplace(@PathVariable("clientOrderId") clientOrderId: String, @RequestBody cancelReplace: CancelReplace, @CookieValue(value = "customerId") customerId: String) : ResponseEntity<NewOrder> {
        logger.info("cancelReplace for customerId $customerId, clientOrderId $clientOrderId: $cancelReplace")

        return ResponseEntity(HttpStatus.ACCEPTED)
    }

    @GetMapping("/{clientOrderId}")
    fun getOrder(@PathVariable("clientOrderId") clientOrderId: String, @CookieValue(value = "customerId") customerId: String) : ResponseEntity<Map<String, NewOrder>> {
        logger.info("getOrder for customerId $customerId, clientOrderId $clientOrderId")
        return ResponseEntity(HttpStatus.OK)

    }

    @DeleteMapping("{clientOrderId}")
    fun cancelOrder(@PathVariable clientOrderId: String, @CookieValue(value = "customerId") customerId: String) : ResponseEntity<Map<String, NewOrder>> {
        logger.info("cancelOrder for customerId $customerId, clientOrderId $clientOrderId")
        return ResponseEntity(HttpStatus.OK)
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(OrderApi::class.java)
    }
}