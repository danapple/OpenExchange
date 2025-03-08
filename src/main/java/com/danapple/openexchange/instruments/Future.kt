package com.danapple.openexchange.instruments

import java.time.LocalDate

class Future(instrumentId: Long, symbol: String, tradingExchange: TradingExchange, underlyingInstrument : Instrument, deliverableQuantity: Int, val expirationDate: LocalDate) : Derivative(instrumentId, symbol, tradingExchange, underlyingInstrument, deliverableQuantity)
