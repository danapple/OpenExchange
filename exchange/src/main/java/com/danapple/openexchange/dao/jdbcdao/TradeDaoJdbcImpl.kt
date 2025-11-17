package com.danapple.openexchange.dao.jdbcdao

import com.danapple.openexchange.dao.TradeDao
import com.danapple.openexchange.entities.trades.Trade
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository

@Repository
open class TradeDaoJdbcImpl(@Qualifier("orderJdbcClients") jdbcClients : List<JdbcClient>,
                            @Qualifier("orderDatabaseConfiguration") databaseConfiguration : DatabaseConfiguration) : TradeDao, ShardedDaoJdbcImpl(
    jdbcClients,
    databaseConfiguration.shardCount
) {
    override fun saveTrade(trade: Trade) {

        val jdbcClient = getJdbcClient(trade.tradeLegs.first().orderState.order.instrument.instrumentId)
        val tradeStatement = jdbcClient.sql(
            """INSERT INTO trade (tradeId, createTime, price) 
                VALUES (:tradeId, :createTime, :price)""")
            .param("tradeId", trade.tradeId)
            .param("createTime", trade.createTime )
            .param("price", trade.price)
        tradeStatement.update()

        trade.tradeLegs.forEach( {
            tradeLeg ->
            val tradeLegStatement = jdbcClient.sql(
                """INSERT INTO trade_leg (tradeLegId, tradeId, orderId, quantity) 
                    VALUES (:tradeLegId, :tradeId, :orderId, :quantity)""")
                .param("tradeLegId", tradeLeg.tradeLegId)
                .param("tradeId", tradeLeg.trade.tradeId)
                .param("orderId", tradeLeg.orderState.order.orderId)
                .param("quantity", tradeLeg.quantity)
            tradeLegStatement.update()
        })
    }
}