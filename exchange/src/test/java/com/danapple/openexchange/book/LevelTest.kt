package com.danapple.openexchange.book

import com.danapple.openexchange.UnitTest
import com.danapple.openexchange.orders.OrderState
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LevelTest : UnitTest() {

    private val level = Level()
    private val orderState1 = OrderState(ORDER_BUY_1, ORDER_CREATETIME_1)
    private val orderState2 = OrderState(ORDER_BUY_2, ORDER_CREATETIME_2)

    @Test
    fun emptyLevelReturnsEmptyList() {
        assertThat(level.getOrderStates()).isEmpty()
    }

    @Test
    fun addedOrderIsReturned() {
        level.addOrder(orderState1)
        assertThat(level.getOrderStates()).containsOnly(orderState1)
    }

    @Test
    fun ordersAreReturnedInInsertOrder() {
        level.addOrder(orderState1)
        level.addOrder(orderState2)
        assertThat(level.getOrderStates()).containsExactly(orderState1, orderState2)
    }

    @Test
    fun lastOrderIsRemoved() {
        level.addOrder(orderState1)
        level.addOrder(orderState2)
        level.removeOrder(orderState2)
        assertThat(level.getOrderStates()).containsExactly(orderState1)
    }

    @Test
    fun firstOrderIsRemoved() {
        level.addOrder(orderState1)
        level.addOrder(orderState2)
        level.removeOrder(orderState1)
        assertThat(level.getOrderStates()).containsExactly(orderState2)
    }

    @Test
    fun levelIsReportedNotEmptyWhenOrdersRemainAfterRemoval() {
        level.addOrder(orderState1)
        level.addOrder(orderState2)
        assertThat(level.removeOrder(orderState1)).isFalse()
    }

    @Test
    fun levelIsReportedNotEmptyWhenAllOrdersRemoved() {
        level.addOrder(orderState1)
        level.addOrder(orderState2)
        assertThat(level.removeOrder(orderState1)).isFalse()
        assertThat(level.removeOrder(orderState2)).isTrue()
    }

    @Test
    fun levelIsReportedNotEmptyWhenNonPresentOrderIsRemoved() {
        level.addOrder(orderState2)
        assertThat(level.removeOrder(orderState1)).isFalse()
    }
}