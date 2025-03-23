package com.danapple.openexchange.dto

import com.fasterxml.jackson.annotation.JsonCreator

class SubmitOrders @JsonCreator constructor(
    val orders: List<Order>
)