package com.danapple.openexchange.entities.instruments

import com.danapple.openexchange.dto.AssetClass
import com.danapple.openexchange.dto.InstrumentStatus
import com.danapple.openexchange.dto.OptionType

class Option(
    instrumentId: Long,
    status: InstrumentStatus,
    symbol: String,
    assetClass: AssetClass,
    description: String,
    expirationTime: Long,
    underlyingInstrumentId: Long,
    valueFactor: Float,
    deliverables: Set<Deliverable>,
    cashDeliverables: Set<CashDeliverable>,
    val optionType: OptionType,
    val strike: Float,
) : Derivative(instrumentId, status, symbol, assetClass, description, expirationTime,
    underlyingInstrumentId, valueFactor,
    deliverables, cashDeliverables
)
