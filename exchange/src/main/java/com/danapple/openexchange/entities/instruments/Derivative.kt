package com.danapple.openexchange.entities.instruments

import com.danapple.openexchange.dto.AssetClass
import com.danapple.openexchange.dto.InstrumentStatus

abstract class Derivative(
    instrumentId: Long,
    status: InstrumentStatus,
    symbol: String,
    assetClass: AssetClass,
    description: String,
    expirationTime: Long,
    val underlyingInstrumentId: Long,
    val valueFactor: Float,
    val deliverables: Set<Deliverable>,
    val cashDeliverables: Set<CashDeliverable>
) : Instrument(instrumentId, status, symbol, assetClass, description, expirationTime)