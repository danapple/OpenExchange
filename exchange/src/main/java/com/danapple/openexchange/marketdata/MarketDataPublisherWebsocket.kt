package com.danapple.openexchange.marketdata

import LastTrade
import com.danapple.openexchange.book.Book
import com.danapple.openexchange.dto.MarketDepth
import com.danapple.openexchange.entities.instruments.Instrument
import com.danapple.openexchange.entities.trades.Trade
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.*
import java.util.*
import java.util.concurrent.atomic.AtomicLong


class MarketDataPublisherWebsocket(
    private val objectMapper: ObjectMapper,
    private val instrument: Instrument,
    interfaceAddress: String,
    multicastGroup: String,
    private val depthPort: Int,
    private val tradePort: Int,
    private val numberOfLevelsToPublish: Int
) : IMarketDataPublisher {
    private val depthSocket : MulticastSocket
    private val tradeSocket : MulticastSocket
    private val multicastGroupAddress : InetAddress
    init {
        multicastGroupAddress = InetAddress.getByName(multicastGroup)
        val netInterface = NetworkInterface.getByName(interfaceAddress)

        val depthGroup = InetSocketAddress(multicastGroupAddress, depthPort)
        depthSocket = MulticastSocket()
        depthSocket.reuseAddress = true
        depthSocket.joinGroup(depthGroup, netInterface);

        val tradeGroup = InetSocketAddress(multicastGroupAddress, tradePort)
        tradeSocket = MulticastSocket()
        tradeSocket.reuseAddress = true
        tradeSocket.joinGroup(tradeGroup, netInterface);
    }
    private val senderId = UUID.randomUUID().toString()
    private val sequenceNumber = AtomicLong()

    fun publishTopOfBook(timestamp: Long, book: Book) {
        val bestBuyPriceLevels = book.getBestBuyPriceLevels(numberOfLevelsToPublish)
        val bestSellPriceLevels = book.getBestSellPriceLevels(numberOfLevelsToPublish)
        val marketDepth = MarketDepth(
            senderId,
            sequenceNumber.getAndIncrement(),
            instrument.instrumentId,
            timestamp,
            bestBuyPriceLevels,
            bestSellPriceLevels
        )
        val bytes = objectMapper.writeValueAsBytes(marketDepth)
        if (logger.isDebugEnabled) {
            logger.debug(marketDepth.toString())
        }
        val packet = DatagramPacket(bytes, bytes.size, multicastGroupAddress, depthPort);
        depthSocket.send(packet)
    }

    fun publishTrades(timestamp: Long, trade: Trade) {
        val lastTrade = LastTrade(senderId,
            sequenceNumber.getAndIncrement(),
            instrument.instrumentId,
            timestamp,
            trade.price,
            trade.tradeLegs.filter { tradeLeg -> tradeLeg.quantity > 0  }.sumOf { tradeLeg -> tradeLeg.quantity })

        val bytes = objectMapper.writeValueAsBytes(lastTrade)
        if (logger.isDebugEnabled) {
            logger.debug(lastTrade.toString())
        }
        val packet = DatagramPacket(bytes, bytes.size, multicastGroupAddress, tradePort);
        tradeSocket.send(packet)
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(MarketDataPublisherWebsocket::class.java)
    }
}