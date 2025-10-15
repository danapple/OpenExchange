package com.danapple.openexchange.dto

import com.fasterxml.jackson.annotation.JsonCreator

data class Instruments @JsonCreator constructor(
    val instruments : Map<Long, Instrument>
)
