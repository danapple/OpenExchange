package com.danapple.openexchange.dao

open interface IdDao {
    fun reserveIdBlock(idType: IdType, blockSize: Int) : ReservedBlock

    enum class IdType {
        ORDER,
        TRADE,
        TRADE_LEG,
        CUSTOMER,
        INSTRUMENT,
    }

    data class ReservedBlock(val firstId : Long, val lastId : Long)
}
