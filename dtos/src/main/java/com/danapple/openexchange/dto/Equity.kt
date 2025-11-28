package com.danapple.openexchange.dto

class Equity(
    instrumentId: Long,
    symbol: String,
    status: InstrumentStatus,
    assetClass: AssetClass,
    description: String,
    expirationTime: Long,
    currencyCode: String
) : Instrument(instrumentId, symbol, status, assetClass, description, expirationTime, currencyCode) {
}