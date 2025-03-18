package com.danapple.openexchange.orders

import com.danapple.openexchange.dao.memoryimplementations.MemoryIdGenerator
import org.springframework.stereotype.Service

@Service
class OrderIdGenerator: MemoryIdGenerator()