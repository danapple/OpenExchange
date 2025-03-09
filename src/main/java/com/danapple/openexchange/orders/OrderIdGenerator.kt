package com.danapple.openexchange.orders

import com.danapple.openexchange.sequencers.MemoryIdGenerator
import org.springframework.stereotype.Service

@Service
class OrderIdGenerator: MemoryIdGenerator()