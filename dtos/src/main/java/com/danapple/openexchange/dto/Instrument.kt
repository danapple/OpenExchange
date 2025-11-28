package com.danapple.openexchange.dto

abstract class Instrument(val instrumentId: Long, val symbol: String, val status: InstrumentStatus,
                          val assetClass: AssetClass, val description: String, val expirationTime: Long,
                          val currencyCode: String)
