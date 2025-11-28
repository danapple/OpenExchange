package com.danapple.openexchange.dto

enum class OptionType {
    CALL,
    PUT,
}

class Option(
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
    cashDeliverables: Set<CashDeliverable>,
    val optionType: OptionType,
    val strike: Float
) : Derivative(instrumentId, symbol, status, assetClass, description, expirationTime, currencyCode,
    underlyingInstrumentId, valueFactor, deliverables, cashDeliverables
)