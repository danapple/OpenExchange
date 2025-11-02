package com.danapple.openexchange.entities.instruments

fun Instrument.toDto() : com.danapple.openexchange.dto.Instrument {
    return com.danapple.openexchange.dto.Instrument(
        instrumentId = instrumentId,
        symbol = symbol,
        status = status,
        assetClass = assetClass,
        description = description,
        expirationTime = expirationTime
    )
}
