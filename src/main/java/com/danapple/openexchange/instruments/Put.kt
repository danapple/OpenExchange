package com.danapple.openexchange.instruments

class Put(symbol: String, tradingExchange: TradingExchange, underlyingInstrument : Instrument, deliverableQuantity: Int) : Derivative(symbol, tradingExchange, underlyingInstrument, deliverableQuantity)
