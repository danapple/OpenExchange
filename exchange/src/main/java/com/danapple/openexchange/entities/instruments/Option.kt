package com.danapple.openexchange.entities.instruments

import java.time.LocalDate

abstract class Option(instrumentId: Long, symbol: String, tradingExchange: TradingExchange, underlyingInstrument : Instrument, deliverableQuantity: Int, val expirationType: ExpirationType, val expirationDate: LocalDate) : Derivative(instrumentId, symbol, tradingExchange, underlyingInstrument, deliverableQuantity)
