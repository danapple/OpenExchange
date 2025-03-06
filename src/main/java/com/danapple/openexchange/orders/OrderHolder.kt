package com.danapple.openexchange.orders

import com.danapple.openexchange.trades.TradeLeg
import java.util.*

internal class OrderHolder(val order: Order)
{
    private var _remainingQuantity: Int = order.quantity()
    private val tradeLegs: MutableList<TradeLeg> = LinkedList()
    internal val remainingQuantity
        get() = this._remainingQuantity


    fun addTradeLeg(tradeLeg: TradeLeg)
    {
        if (tradeLeg.quantity > _remainingQuantity)
        {
            throw IllegalArgumentException("TradeLeg quantity exceeds order remaining quantity")
        }
        tradeLegs.add(tradeLeg)
        _remainingQuantity -= tradeLeg.quantity
    }

}