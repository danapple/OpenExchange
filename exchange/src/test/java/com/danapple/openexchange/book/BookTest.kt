package com.danapple.openexchange.book

import com.danapple.openexchange.TestConstants.Companion.CL_ORD_BUY_1
import com.danapple.openexchange.TestConstants.Companion.CL_ORD_SELL_1
import com.danapple.openexchange.TestConstants.Companion.CUSTOMER
import com.danapple.openexchange.TestConstants.Companion.INSTRUMENT_1
import com.danapple.openexchange.TestConstants.Companion.ORDER_BUY_1
import com.danapple.openexchange.TestConstants.Companion.ORDER_BUY_2
import com.danapple.openexchange.TestConstants.Companion.ORDER_FACTORY
import com.danapple.openexchange.TestConstants.Companion.ORDER_QUANTITY_1
import com.danapple.openexchange.TestConstants.Companion.ORDER_SELL_1
import com.danapple.openexchange.TestConstants.Companion.ORDER_SELL_2
import com.danapple.openexchange.TestConstants.Companion.ORDER_TIMESTAMP_2
import com.danapple.openexchange.dto.OrderStatus
import com.danapple.openexchange.orders.OrderState
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class BookTest {
    private val book = Book()
    private val orderStateBuy1 = OrderState(ORDER_BUY_1)
    private val orderStateBuy2 = OrderState(ORDER_BUY_2)

    private val orderStateSell1 = OrderState(ORDER_SELL_1)
    private val orderStateSell2 = OrderState(ORDER_SELL_2)

    private val orderBuy1A = ORDER_FACTORY.createOrder(CUSTOMER, ORDER_TIMESTAMP_2, CL_ORD_BUY_1, INSTRUMENT_1, BigDecimal.ONE, ORDER_QUANTITY_1)
    private val orderStateBuy1A = OrderState(orderBuy1A)

    private val orderSell1A = ORDER_FACTORY.createOrder(CUSTOMER, ORDER_TIMESTAMP_2, CL_ORD_SELL_1, INSTRUMENT_1, BigDecimal.ONE, -ORDER_QUANTITY_1)
    private val orderStateSell1A = OrderState(orderSell1A)

    @Test
    fun fillsOrder() {
        book.addOrder(orderStateBuy1)
        book.fillOrder(orderStateBuy1)
        assertThat(orderStateBuy1.orderStatus).isEqualTo(OrderStatus.FILLED)
    }

    @Test
    fun cancelsOrder() {
        book.addOrder(orderStateBuy1)
        book.cancelOrder(orderStateBuy1)
        assertThat(orderStateBuy1.orderStatus).isEqualTo(OrderStatus.CANCELED)
    }

    @Test
    fun emptyBookReturnsNoLevels() {
        assertThat(book.getMatchingOppositeLevels(orderStateBuy2)).isEmpty()
    }

    @Test
    fun doesNotMatchBuyForSameSideBetterPrice() {
        book.addOrder(orderStateBuy1)
        assertThat(book.getMatchingOppositeLevels(orderStateBuy2)).isEmpty()
    }

    @Test
    fun doesNotMatchBuyForSameSideWorsePrice() {
        book.addOrder(orderStateBuy2)
        assertThat(book.getMatchingOppositeLevels(orderStateBuy1)).isEmpty()
    }

    @Test
    fun doesNotMatchSellForSameSideBetterProce() {
        book.addOrder(orderStateBuy2)
        assertThat(book.getMatchingOppositeLevels(orderStateBuy1)).isEmpty()
    }

    @Test
    fun doesNotMatchSellForSameSideWorsePrice() {
        book.addOrder(orderStateBuy2)
        assertThat(book.getMatchingOppositeLevels(orderStateBuy2)).isEmpty()
    }

    @Test
    fun matchesSellForSamePrice() {
        book.addOrder(orderStateBuy2)
        val matchingOppositeLevels = book.getMatchingOppositeLevels(orderStateSell2)
        assertThat(matchingOppositeLevels).isNotEmpty()
        assertThat(matchingOppositeLevels.first.getOrderStates()).containsExactly(orderStateBuy2)
    }

    @Test
    fun doesNotMatchSellForWorsePrice() {
        book.addOrder(orderStateBuy1)
        val matchingOppositeLevels = book.getMatchingOppositeLevels(orderStateSell2)
        assertThat(matchingOppositeLevels).isEmpty()
    }

    @Test
    fun matchesBuyForSamePrice() {
        book.addOrder(orderStateSell2)
        val matchingOppositeLevels = book.getMatchingOppositeLevels(orderStateBuy2)
        assertThat(matchingOppositeLevels).isNotEmpty()
        assertThat(matchingOppositeLevels.first.getOrderStates()).containsExactly(orderStateSell2)
    }

    @Test
    fun doesNotMatchBuyForWorsePrice() {
        book.addOrder(orderStateSell2)
        val matchingOppositeLevels = book.getMatchingOppositeLevels(orderStateBuy1)
        assertThat(matchingOppositeLevels).isEmpty()
    }

    @Test
    fun matchesBuyAllWorseLevels() {
        book.addOrder(orderStateSell1)
        book.addOrder(orderStateSell2)
        val matchingOppositeLevels = book.getMatchingOppositeLevels(orderStateBuy2)
        assertThat(matchingOppositeLevels).hasSize(2)
        val levelsArray = matchingOppositeLevels.toTypedArray()
        assertThat(levelsArray[0].getOrderStates()).containsExactly(orderStateSell1)
        assertThat(levelsArray[1].getOrderStates()).containsExactly(orderStateSell2)
    }

    @Test
    fun matchesSellAllWorseLevels() {
        book.addOrder(orderStateBuy1)
        book.addOrder(orderStateBuy2)
        val matchingOppositeLevels = book.getMatchingOppositeLevels(orderStateSell1)
        assertThat(matchingOppositeLevels).hasSize(2)
        val levelsArray = matchingOppositeLevels.toTypedArray()
        assertThat(levelsArray[0].getOrderStates()).containsExactly(orderStateBuy2)
        assertThat(levelsArray[1].getOrderStates()).containsExactly(orderStateBuy1)
    }

    @Test
    fun withinLevelMatchesBuyInInsertOrder() {
        book.addOrder(orderStateBuy1)
        book.addOrder(orderStateBuy1A)
        val matchingOppositeLevels = book.getMatchingOppositeLevels(orderStateSell1)
        assertThat(matchingOppositeLevels).hasSize(1)
        val levelsArray = matchingOppositeLevels.toTypedArray()
        assertThat(levelsArray[0].getOrderStates()).containsExactly(orderStateBuy1, orderStateBuy1A)
    }

    @Test
    fun withinLevelMatchesSellInInsertOrder() {
        book.addOrder(orderStateSell1)
        book.addOrder(orderStateSell1A)
        val matchingOppositeLevels = book.getMatchingOppositeLevels(orderStateBuy1)
        assertThat(matchingOppositeLevels).hasSize(1)
        val levelsArray = matchingOppositeLevels.toTypedArray()
        assertThat(levelsArray[0].getOrderStates()).containsExactly(orderStateSell1, orderStateSell1A)
    }

    @Test
    fun matchesBuyInPriceTimePriorityFor() {
        book.addOrder(orderStateSell1)
        book.addOrder(orderStateSell1A)
        book.addOrder(orderStateSell2)
        val matchingOppositeLevels = book.getMatchingOppositeLevels(orderStateBuy2)
        assertThat(matchingOppositeLevels).hasSize(2)
        val levelsArray = matchingOppositeLevels.toTypedArray()
        assertThat(levelsArray[0].getOrderStates()).containsExactly(orderStateSell1, orderStateSell1A)
        assertThat(levelsArray[1].getOrderStates()).containsExactly(orderStateSell2)
    }

    @Test
    fun matchesSellInPriceTimePriorityFor() {
        book.addOrder(orderStateBuy1)
        book.addOrder(orderStateBuy1A)
        book.addOrder(orderStateBuy2)
        val matchingOppositeLevels = book.getMatchingOppositeLevels(orderStateSell1)
        assertThat(matchingOppositeLevels).hasSize(2)
        val levelsArray = matchingOppositeLevels.toTypedArray()
        assertThat(levelsArray[0].getOrderStates()).containsExactly(orderStateBuy2)
        assertThat(levelsArray[1].getOrderStates()).containsExactly(orderStateBuy1, orderStateBuy1A)
    }
}