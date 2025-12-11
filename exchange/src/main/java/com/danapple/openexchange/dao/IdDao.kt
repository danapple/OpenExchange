package com.danapple.openexchange.dao

interface IdDao {
    fun reserveIdBlock(idType: IdType, blockSize: Int): ReservedBlock

    enum class IdType {
        ORDER,
        TRADE,
        TRADE_LEG,
        CUSTOMER,
        INSTRUMENT,
        ORDER_STATE_HISTORY,
    }

    data class ReservedBlock(val firstId: Long, val lastId: Long)
}
