package com.danapple.openexchange.dto

class Future(
    instrumentId: Long,
    symbol: String,
    status: InstrumentStatus,
    assetClass: AssetClass,
    description: String,
    expirationTime: Long,
    currencyCode: String,
    underlyingInstrumentId: Long,
    valueFactor: Float,
    deliverables: Set<Deliverable>,
    cashDeliverables: Set<CashDeliverable>
) : Derivative(instrumentId, symbol, status, assetClass, description, expirationTime, currencyCode,
    underlyingInstrumentId, valueFactor, deliverables, cashDeliverables)