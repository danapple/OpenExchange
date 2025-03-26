package com.danapple.openexchange.dto

import com.fasterxml.jackson.annotation.JsonCreator

data class OrderStates @JsonCreator constructor(
    val orderStates : List<OrderState>
)
