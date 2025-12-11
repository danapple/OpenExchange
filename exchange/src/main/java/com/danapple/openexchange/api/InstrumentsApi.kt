package com.danapple.openexchange.api

import com.danapple.openexchange.dao.InstrumentDao
import com.danapple.openexchange.dto.Instruments
import com.danapple.openexchange.entities.instruments.toDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/instruments")
class InstrumentsApi(private val instrumentDao: InstrumentDao) : BaseApi() {
    @GetMapping
    fun getInstruments(): ResponseEntity<Instruments> {
        val customer = getCustomer()
        if (logger.isDebugEnabled) {
            logger.debug("getInstruments for customerId ${customer.customerId}")
        }
        val instruments = instrumentDao.getAllInstruments().map { it.toDto() }

        val instrumentMap = instruments.associateBy { it.instrumentId }
        return ResponseEntity(Instruments(instrumentMap), HttpStatus.OK)
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(InstrumentsApi::class.java)
    }
}
