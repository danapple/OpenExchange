package com.danapple.openexchange.memoryimplementations

import com.danapple.openexchange.sequencers.IdGenerator
import java.util.concurrent.atomic.AtomicLong

abstract class MemoryIdGenerator: IdGenerator {
    companion object {
        val nextId = AtomicLong()
    }

    override fun getId(): Long {
        return nextId.getAndIncrement()
    }
}