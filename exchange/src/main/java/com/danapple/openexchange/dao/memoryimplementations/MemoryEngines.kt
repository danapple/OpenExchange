package com.danapple.openexchange.dao.memoryimplementations

import com.danapple.openexchange.dao.InstrumentDao
import com.danapple.openexchange.engine.Engine
import com.danapple.openexchange.engine.EngineFactory
import com.danapple.openexchange.entities.instruments.Instrument
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MemoryEngines(@Autowired val engineFactory: EngineFactory, @Autowired val instrumentDao : InstrumentDao) {
    @Bean
    open fun engines() : Map<Instrument, Engine> {
        val instruments = (0L .. 100L).map { instrumentDao.getInstrument(it) }.toSet()
        return engineFactory.createEngines(instruments)
    }
}