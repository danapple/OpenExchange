package com.danapple.openexchange.trades;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.danapple.openexchange.orders.Order;
import com.danapple.openexchange.orders.Side;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class TradeTest
{
    private static final Order ORDER_1 = Order.OrderFactory.createOrder("ClOrdId 1", "C", Side.BUY, 4, BigDecimal.ONE);
    private static final Order ORDER_2 = Order.OrderFactory.createOrder("ClOrdId 2", "C", Side.SELL, 3, BigDecimal.ONE);

    @Test
    public void acceptsBalancedTrade()
    {
        TradeLeg tradeLeg1 = TradeLeg.TradeLegFactory.createTradeLeg(4, ORDER_1);
        TradeLeg tradeLeg2 = TradeLeg.TradeLegFactory.createTradeLeg(4, ORDER_2);
        Trade trade = Trade.TradeFactory.createTrade(BigDecimal.ONE, Set.of(tradeLeg1, tradeLeg2));
        assertThat(trade.getTradeLegs());
    }

    @Test
    public void rejectsUnbalancedTrade()
    {
        TradeLeg tradeLeg1 = TradeLeg.TradeLegFactory.createTradeLeg(4, ORDER_1);
        TradeLeg tradeLeg2 = TradeLeg.TradeLegFactory.createTradeLeg(3, ORDER_2);
        assertThatThrownBy(() -> Trade.TradeFactory.createTrade(BigDecimal.ONE, Set.of(tradeLeg1, tradeLeg2)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("must be 0, not 1");
    }
}
