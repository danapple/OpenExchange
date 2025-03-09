package com.danapple.openexchange.sequencers

import java.util.concurrent.atomic.AtomicLong

abstract class MemoryIdGenerator: IdGenerator {
    companion object {
        val nextId = AtomicLong()
    }

    override fun getId(): Long {
        return nextId.getAndIncrement()
    }
}