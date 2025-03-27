package com.danapple.openexchange.dao

class DaoIdGenerator (private val idType: IdDao.IdType, private val idDao : IdDao, private val blockSize : Int) :
    IdGenerator {
    var lastReservedId : Long = 0
    var lastIssuedId : Long = 0
    @Synchronized override fun getNextId(): Long {
        if (lastIssuedId < lastReservedId) {
            return ++lastIssuedId;
        }
        val newBlock = idDao.reserveIdBlock(idType, blockSize)
        lastIssuedId = newBlock.firstId
        lastReservedId = newBlock.lastId
        return lastIssuedId
    }
}
