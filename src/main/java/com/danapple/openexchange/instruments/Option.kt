package com.danapple.openexchange.instruments

import java.time.LocalDate

abstract class Option(symbol: String, tradingExchange: TradingExchange, underlyingInstrument : Instrument, deliverableQuantity: Int, val expirationType: ExpirationType, val expirationDate: LocalDate) : Derivative(symbol, tradingExchange, underlyingInstrument, deliverableQuantity)
