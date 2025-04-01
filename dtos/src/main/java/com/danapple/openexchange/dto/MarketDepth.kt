package com.danapple.openexchange.dto

data class MarketDepth(val senderId : String,
                       val sequenceNumber : Long,
                       val instrumentId : Long,
                       val createTime : Long,
                       val buys : List<PriceLevel>,
                       val sells : List<PriceLevel>) {
    override fun toString(): String {
        return """MarketDepth(senderId='$senderId', sequenceNumber=$sequenceNumber, instrumentId=$instrumentId, 
            createTime=$createTime, buys=$buys, sells=$sells)"""
    }
}