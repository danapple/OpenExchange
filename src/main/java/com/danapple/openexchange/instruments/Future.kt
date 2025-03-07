package com.danapple.openexchange.instruments

import java.time.LocalDate

class Future(symbol: String, tradingExchange: TradingExchange, underlyingInstrument : Instrument, deliverableQuantity: Int, val expirationDate: LocalDate) : Derivative(symbol, tradingExchange, underlyingInstrument, deliverableQuantity)
