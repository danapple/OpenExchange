package com.danapple.openexchange.dto

abstract class Derivative(
    instrumentId: Long,
    symbol: String,
    status: InstrumentStatus,
    assetClass: AssetClass,
    description: String,
    expirationTime: Long,
    val underlyingInstrumentId: Long,
    val deliverables: Set<Deliverable>,
    val cashDeliverables: Set<CashDeliverable>
) : Instrument(instrumentId, symbol, status, assetClass, description, expirationTime) {
}