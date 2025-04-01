import java.math.BigDecimal

data class LastTrade(val senderId : String,
                     val sequenceNumber : Long,
                     val instrumentId : Long,
                     val createTime : Long,
                     val price : BigDecimal,
                     val quantity : Int)