package com.danapple.openexchange.trades

import com.danapple.openexchange.sequencers.MemoryIdGenerator
import org.springframework.stereotype.Service

@Service
class TradeLegIdGenerator: MemoryIdGenerator()