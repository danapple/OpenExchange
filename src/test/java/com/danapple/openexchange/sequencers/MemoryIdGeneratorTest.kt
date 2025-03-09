package com.danapple.openexchange.sequencers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MemoryIdGeneratorTest {

    class TestIdGenerator: MemoryIdGenerator()

    @Test
    fun generatesId() {
        testIdGenerator.getId()

        assertThat(testIdGenerator.getId()).isGreaterThanOrEqualTo(0L)
    }

    @Test
    fun generatesDistinctIds() {
        val id0 = testIdGenerator.getId()
        val id1 = testIdGenerator.getId()

        assertThat(id1).isNotEqualTo(id0)
    }

    companion object {
        val testIdGenerator = TestIdGenerator()
    }
}