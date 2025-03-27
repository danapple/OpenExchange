package com.danapple.openexchange.dao

interface IdGenerator {
    fun getNextId(): Long
}