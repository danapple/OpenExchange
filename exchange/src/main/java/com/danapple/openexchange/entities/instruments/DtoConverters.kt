package com.danapple.openexchange.entities.instruments

import com.danapple.openexchange.dto.OptionType

fun Instrument.toDto() : com.danapple.openexchange.dto.Instrument {
    return when (this) {
        is Equity -> {
            this.toDto()
        }
        is Option -> {
            this.toDto()
        }
        is Future -> {
            this.toDto()
        }
        else -> TODO()
    }
}

fun Equity.toDto() : com.danapple.openexchange.dto.Equity {
    return com.danapple.openexchange.dto.Equity(
        instrumentId = instrumentId,
        symbol = symbol,
        status = status,
        assetClass = assetClass,
        description = description,
        expirationTime = expirationTime
    )
}

fun Option.toDto() : com.danapple.openexchange.dto.Option {
    val deliverables = deliverables.map { deliverable -> deliverable.toDto() }.toSet()
    val cashDeliverables = cashDeliverables.map { cashDeliverable -> cashDeliverable.toDto() }.toSet()
    return com.danapple.openexchange.dto.Option(
        instrumentId = instrumentId,
        symbol = symbol,
        status = status,
        assetClass = assetClass,
        description = description,
        expirationTime = expirationTime,
        underlyingInstrumentId = underlyingInstrumentId,
        optionType = OptionType.valueOf(optionType.name),
        strike = strike,
        deliverables = deliverables,
        cashDeliverables = cashDeliverables
    )
}

fun Future.toDto() : com.danapple.openexchange.dto.Future {
    val deliverables = deliverables.map { deliverable -> deliverable.toDto() }.toSet()
    val cashDeliverables = cashDeliverables.map { cashDeliverable -> cashDeliverable.toDto() }.toSet()
    return com.danapple.openexchange.dto.Future(
        instrumentId = instrumentId,
        symbol = symbol,
        status = status,
        assetClass = assetClass,
        description = description,
        expirationTime = expirationTime,
        underlyingInstrumentId = underlyingInstrumentId,
        deliverables = deliverables,
        cashDeliverables = cashDeliverables
    )
}

fun Deliverable.toDto() : com.danapple.openexchange.dto.Deliverable {
    return com.danapple.openexchange.dto.Deliverable(
        instrumentId = instrumentId,
        deliverableInstrumentId = deliverableInstrumentId,
        quantity = quantity
    )
}

fun CashDeliverable.toDto() : com.danapple.openexchange.dto.CashDeliverable {
    return com.danapple.openexchange.dto.CashDeliverable(
        instrumentId = instrumentId,
        value = value
    )
}