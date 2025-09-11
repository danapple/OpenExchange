package com.danapple.openexchange.marketdata

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.SimpMessagingTemplate

@Configuration
open class MarketDataPublisherFactory(
    private val objectMapper: ObjectMapper,
    private val simpMessagingTemplate: SimpMessagingTemplate,
    @Value("\${openexchange.marketDataPublisher.interfaceAddress}") private val interfaceAddress : String,
    @Value("\${openexchange.marketDataPublisher.multicastGroup}") private val multicastGroup : String,
    @Value("\${openexchange.marketDataPublisher.depthPort}") private val depthPort : Int,
    @Value("\${openexchange.marketDataPublisher.tradePort}") private val tradePort : Int,
    @Value("\${openexchange.marketDataPublisher.numberOfLevelsToPublish}") private val numberOfLevelsToPublish : Int) {

    @Bean
    open fun createMarketDataPublisher(): MarketDataPublisher {
        val mdpm = MarketDataPublisherMulticast(
            objectMapper,
            interfaceAddress,
            multicastGroup,
            depthPort,
            tradePort,
            numberOfLevelsToPublish
        )
        val mdpw = MarketDataPublisherWebsocket(
            numberOfLevelsToPublish,
            simpMessagingTemplate
        )
        return MarketDataPublisherDistributor(setOf(mdpm, mdpw))
    }
}