package com.danapple.openexchange.dto

class ExecutionsTopicWrapper {
    val execution: Execution?
    val orderState: OrderState?

    constructor(execution: Execution) {
        this.execution = execution
        orderState = null
    }

    constructor(orderState: OrderState) {
        this.execution = null
        this.orderState = orderState
    }

}