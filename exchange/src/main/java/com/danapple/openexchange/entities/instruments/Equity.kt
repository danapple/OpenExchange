package com.danapple.openexchange.entities.instruments

import com.danapple.openexchange.dto.AssetClass
import com.danapple.openexchange.dto.InstrumentStatus

class Equity(
    instrumentId: Long,
    status: InstrumentStatus,
    symbol: String,
    assetClass: AssetClass,
    description: String,
    expirationTime: Long
) : Instrument(instrumentId, status, symbol, assetClass, description, expirationTime)