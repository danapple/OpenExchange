package com.danapple.openexchange.dto

class Future(
    instrumentId: Long,
    symbol: String,
    status: InstrumentStatus,
    assetClass: AssetClass,
    description: String,
    expirationTime: Long,
    underlyingInstrumentId: Long,
    deliverables: Set<Deliverable>,
    cashDeliverables: Set<CashDeliverable>
) : Derivative(instrumentId, symbol, status, assetClass, description, expirationTime,
    underlyingInstrumentId, deliverables, cashDeliverables
)