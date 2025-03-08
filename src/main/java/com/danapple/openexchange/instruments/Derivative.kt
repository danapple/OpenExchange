package com.danapple.openexchange.instruments

abstract class Derivative(instrumentId: Long, symbol: String, tradingExchange: TradingExchange, val underlyingInstrument : Instrument, val deliverableQuantity: Int) : Instrument(instrumentId, symbol, tradingExchange)