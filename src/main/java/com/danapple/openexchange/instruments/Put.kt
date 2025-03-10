package com.danapple.openexchange.instruments

class Put(instrumentId: Long, symbol: String, tradingExchange: TradingExchange, underlyingInstrument : Instrument, deliverableQuantity: Int) : Derivative(instrumentId, symbol, tradingExchange, underlyingInstrument, deliverableQuantity)
