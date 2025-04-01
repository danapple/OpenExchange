package com.danapple.openexchange.marketdata

import com.danapple.openexchange.entities.instruments.Instrument
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class MarketDataPublisherFactory(
    private val objectMapper: ObjectMapper,
    @Value("\${openexchange.marketDataPublisher.interfaceAddress}") private val interfaceAddress : String,
    @Value("\${openexchange.marketDataPublisher.multicastGroup}") private val multicastGroup : String,
    @Value("\${openexchange.marketDataPublisher.depthPort}") private val depthPort : Int,
    @Value("\${openexchange.marketDataPublisher.tradePort}") private val tradePort : Int,
    @Value("\${openexchange.marketDataPublisher.numberOfLevelsToPublish}") private val numberOfLevelsToPublish : Int) {
    fun createMarketDataPublisher(instrument : Instrument): MarketDataPublisher {
        return MarketDataPublisher(
            objectMapper,
            instrument,
            interfaceAddress,
            multicastGroup,
            depthPort,
            tradePort,
            numberOfLevelsToPublish
        )
    }
}