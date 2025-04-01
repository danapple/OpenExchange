package com.danapple.openexchange.book


import com.danapple.openexchange.UnitTest
import com.danapple.openexchange.dto.OrderStatus
import com.danapple.openexchange.orders.OrderState
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class BookTest : UnitTest() {
    private val book = Book()
    private val orderStateBuy1 = OrderState(ORDER_BUY_1)
    private val orderStateBuy2 = OrderState(ORDER_BUY_2)

    private val orderStateSell1 = OrderState(ORDER_SELL_1)
    private val orderStateSell2 = OrderState(ORDER_SELL_2)

    private val orderBuy1A = orderFactory.createOrder(CUSTOMER, ORDER_CREATETIME_2, CL_ORD_BUY_1, INSTRUMENT_1, BigDecimal.ONE, ORDER_QUANTITY_1)
    private val orderStateBuy1A = OrderState(orderBuy1A)

    private val orderSell1A = orderFactory.createOrder(CUSTOMER, ORDER_CREATETIME_2, CL_ORD_SELL_1, INSTRUMENT_1, BigDecimal.ONE, -ORDER_QUANTITY_1)
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

    @Test
    fun getsEmptyBestBuyPriceLevels() {
        assertThat(book.getBestBuyPriceLevels(4)).isEmpty()
    }

    @Test
    fun getsEmptyBestSellPriceLevels() {
        assertThat(book.getBestSellPriceLevels(4)).isEmpty()
    }

    @Test
    fun getsEmptyBestBuyPriceLevelsWhenSellsPresent() {
        book.addOrder(orderStateSell1)
        assertThat(book.getBestBuyPriceLevels(4)).isEmpty()
    }

    @Test
    fun getsEmptyBestSellPriceLevelsWhenBuysPresent() {
        book.addOrder(orderStateBuy1)
        assertThat(book.getBestSellPriceLevels(4)).isEmpty()
    }

    @Test
    fun getsSingularBestBuyPriceLevels() {
        book.addOrder(orderStateBuy1)
        book.addOrder(orderStateBuy1A)

        val bestBuyPriceLevels = book.getBestBuyPriceLevels(4)

        assertThat(bestBuyPriceLevels).hasSize(1)
        assertThat(bestBuyPriceLevels.first().price).isEqualTo(orderStateBuy1.order.price)
        assertThat(bestBuyPriceLevels.first().quantity).isEqualTo(orderStateBuy1.order.quantity + orderStateBuy1A.order.quantity)
    }

    @Test
    fun getsSingularBestSellPriceLevels() {
        book.addOrder(orderStateSell1)
        book.addOrder(orderStateSell1A)

        val bestSellPriceLevels = book.getBestSellPriceLevels(4)

        assertThat(bestSellPriceLevels).hasSize(1)
        assertThat(bestSellPriceLevels.first().price).isEqualTo(orderStateSell1.order.price)
        assertThat(bestSellPriceLevels.first().quantity).isEqualTo(orderStateSell1.order.quantity + orderStateSell1A.order.quantity)
    }

    @Test
    fun getsFirstBestBuyPriceLevels() {
        book.addOrder(orderStateBuy1)
        book.addOrder(orderStateBuy1A)
        book.addOrder(orderStateBuy2)

        val bestBuyPriceLevels = book.getBestBuyPriceLevels(1)

        assertThat(bestBuyPriceLevels).hasSize(1)
        assertThat(bestBuyPriceLevels.first().price).isEqualTo(orderStateBuy2.order.price)
        assertThat(bestBuyPriceLevels.first().quantity).isEqualTo(orderStateBuy2.order.quantity)
    }

    @Test
    fun getsFirstBestSellPriceLevels() {
        book.addOrder(orderStateSell1)
        book.addOrder(orderStateSell1A)
        book.addOrder(orderStateSell2)

        val bestBuyPriceLevels = book.getBestSellPriceLevels(1)

        assertThat(bestBuyPriceLevels).hasSize(1)
        assertThat(bestBuyPriceLevels.first().price).isEqualTo(orderStateSell1.order.price)
        assertThat(bestBuyPriceLevels.first().quantity).isEqualTo(orderStateSell1.order.quantity + orderStateSell1A.order.quantity)
    }

    @Test
    fun getsAllBestBuyPriceLevels() {
        book.addOrder(orderStateBuy1)
        book.addOrder(orderStateBuy1A)
        book.addOrder(orderStateBuy2)

        val bestBuyPriceLevels = book.getBestBuyPriceLevels(5)

        assertThat(bestBuyPriceLevels).hasSize(2)
        assertThat(bestBuyPriceLevels.first().price).isEqualTo(orderStateBuy2.order.price)
        assertThat(bestBuyPriceLevels.first().quantity).isEqualTo(orderStateBuy2.order.quantity)

        assertThat(bestBuyPriceLevels.last().price).isEqualTo(orderStateBuy1.order.price)
        assertThat(bestBuyPriceLevels.last().quantity).isEqualTo(orderStateBuy1.order.quantity + orderStateBuy1A.order.quantity)
    }

    @Test
    fun getsAllBestSellPriceLevels() {
        book.addOrder(orderStateSell1)
        book.addOrder(orderStateSell1A)
        book.addOrder(orderStateSell2)

        val bestBuyPriceLevels = book.getBestSellPriceLevels(2)

        assertThat(bestBuyPriceLevels).hasSize(2)
        assertThat(bestBuyPriceLevels.first().price).isEqualTo(orderStateSell1.order.price)
        assertThat(bestBuyPriceLevels.first().quantity).isEqualTo(orderStateSell1.order.quantity + orderStateSell1A.order.quantity)

        assertThat(bestBuyPriceLevels.last().price).isEqualTo(orderStateSell2.order.price)
        assertThat(bestBuyPriceLevels.last().quantity).isEqualTo(orderStateSell2.order.quantity)
    }
}