package com.danapple.openexchange

import com.danapple.openexchange.dao.CustomerDao
import com.danapple.openexchange.dao.InstrumentDao
import com.danapple.openexchange.dao.OrderDao
import com.danapple.openexchange.dao.OrderQueryDao
import com.danapple.openexchange.dto.AssetClass
import com.danapple.openexchange.dto.InstrumentStatus
import com.danapple.openexchange.entities.customers.Customer
import com.danapple.openexchange.entities.instruments.Instrument
import com.danapple.openexchange.orders.OrderFactory
import java.math.BigDecimal
import java.time.Clock

open class ExchangeTest constructor(val orderDao : OrderDao,
                                    val orderQueryDao : OrderQueryDao,
                                    val customerDao : CustomerDao,
                                    val instrumentDao : InstrumentDao,
                                    val orderFactory : OrderFactory) {

    val CLOCK = Clock.systemDefaultZone()
    val now = CLOCK.millis()

    val INSTRUMENT_ID_1 = 0L
    val INSTRUMENT_SYMBOL_1 = "INS1";
    val INSTRUMENT_DESCRIPTION_1 = "INS1 Description";
    val INSTRUMENT_1  = Instrument(
        INSTRUMENT_ID_1,
        status = InstrumentStatus.ACTIVE,
        symbol = INSTRUMENT_SYMBOL_1,
        assetClass = AssetClass.EQUITY,
        description = INSTRUMENT_DESCRIPTION_1,
        expirationTime = System.currentTimeMillis() + 86400 * 1000
    )

    val CUSTOMER_ID_1 = 1L
    val CUSTOMER_KEY_1 = "BrokerA"
    val CUSTOMER = Customer(CUSTOMER_ID_1, CUSTOMER_KEY_1)
    val CL_ORD_BUY_1 = "ClOrdBuy1.$now"
    val CL_ORD_BUY_2 = "ClOrdBuy2.$now"

    val CL_ORD_SELL_1 = "ClOrdSell1.$now"
    val CL_ORD_SELL_2 = "ClOrdSell2.$now"

    val ORDER_QUANTITY_1 = 10
    val ORDER_QUANTITY_2 = 20

    val ORDER_CREATETIME_1 = 100101L
    val ORDER_CREATETIME_2 = 200202L

    val ORDER_BUY_1 = orderFactory.createOrder(CUSTOMER, ORDER_CREATETIME_1, CL_ORD_BUY_1, INSTRUMENT_1, BigDecimal.ONE, ORDER_QUANTITY_1)
    val ORDER_BUY_2 = orderFactory.createOrder(CUSTOMER, ORDER_CREATETIME_2, CL_ORD_BUY_2, INSTRUMENT_1, BigDecimal.TWO, ORDER_QUANTITY_2)

    val ORDER_SELL_1 = orderFactory.createOrder(CUSTOMER, ORDER_CREATETIME_1, CL_ORD_SELL_1, INSTRUMENT_1, BigDecimal.ONE, -ORDER_QUANTITY_1)
    val ORDER_SELL_2 = orderFactory.createOrder(CUSTOMER, ORDER_CREATETIME_2, CL_ORD_SELL_2, INSTRUMENT_1, BigDecimal.TWO, -ORDER_QUANTITY_2)
}