package com.danapple.openexchange.entities.instruments

import com.danapple.openexchange.dto.AssetClass
import com.danapple.openexchange.dto.InstrumentStatus

abstract class Instrument(
    val instrumentId: Long,
    val status: InstrumentStatus,
    val symbol: String,
    val assetClass: AssetClass,
    val description: String,
    val expirationTime: Long,
    val currencyCode: String
)
