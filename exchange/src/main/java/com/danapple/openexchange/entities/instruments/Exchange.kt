package com.danapple.openexchange.entities.instruments

import java.util.TimeZone

abstract class Exchange(val exchangeId: Long, val exchangeSymbol: String, val timezone: TimeZone)
