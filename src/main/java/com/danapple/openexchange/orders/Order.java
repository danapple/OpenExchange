package com.danapple.openexchange.orders;

import java.math.BigDecimal;

public record Order(long orderId, String symbol, Side side, int quantity, BigDecimal price)
{}
