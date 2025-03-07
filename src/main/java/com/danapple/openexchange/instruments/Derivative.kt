package com.danapple.openexchange.instruments

abstract class Derivative(symbol: String, tradingExchange: TradingExchange, val underlyingInstrument : Instrument, val deliverableQuantity: Int) : Instrument(symbol, tradingExchange)