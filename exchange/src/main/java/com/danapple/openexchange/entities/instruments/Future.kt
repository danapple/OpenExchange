package com.danapple.openexchange.entities.instruments

import com.danapple.openexchange.dto.AssetClass
import com.danapple.openexchange.dto.InstrumentStatus

class Future(
    instrumentId: Long,
    status: InstrumentStatus,
    symbol: String,
    assetClass: AssetClass,
    description: String,
    expirationTime: Long,
    currencyCode: String,
    underlyingInstrumentId: Long,
    valueFactor: Float,
    deliverables: Set<Deliverable>,
    cashDeliverables: Set<CashDeliverable>,
) : Derivative(
    instrumentId, status, symbol, assetClass, description, expirationTime, currencyCode,
    underlyingInstrumentId, valueFactor, deliverables, cashDeliverables
)