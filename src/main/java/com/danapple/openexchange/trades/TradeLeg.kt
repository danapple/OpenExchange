package com.danapple.openexchange.trades

import com.danapple.openexchange.orders.Order

data class TradeLeg(val quantity: Int, val order: Order)
