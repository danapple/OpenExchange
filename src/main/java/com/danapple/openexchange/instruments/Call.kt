package com.danapple.openexchange.instruments

class Call(instrumentId: Long, symbol: String, tradingExchange: TradingExchange, underlyingInstrument : Instrument, deliverableQuantity: Int) : Derivative(instrumentId, symbol, tradingExchange, underlyingInstrument, deliverableQuantity)
