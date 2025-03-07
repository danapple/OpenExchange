package com.danapple.openexchange.instruments

class Call(symbol: String, tradingExchange: TradingExchange, underlyingInstrument : Instrument, deliverableQuantity: Int) : Derivative(symbol, tradingExchange, underlyingInstrument, deliverableQuantity)
