package com.danapple.openexchange

import com.danapple.openexchange.dao.IdGenerator
import java.util.concurrent.atomic.AtomicLong

class MemoryIdGenerator : IdGenerator {
    private val nextId = AtomicLong()
    override fun getNextId(): Long {
        return nextId.getAndIncrement()
    }
}
