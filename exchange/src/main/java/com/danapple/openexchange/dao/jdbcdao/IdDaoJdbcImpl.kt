package com.danapple.openexchange.dao.jdbcdao

import com.danapple.openexchange.dao.IdDao
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository

@Repository
open class IdDaoJdbcImpl(@Qualifier("idJdbcClient") private val jdbcClient: JdbcClient) : IdDao {
    override fun reserveIdBlock(idType: IdDao.IdType, blockSize: Int): IdDao.ReservedBlock {
        var lastReservedId = getLastReservedId(idType)
        if (logger.isTraceEnabled) {
            logger.trace("First attempt ID get for $idType retrieved $lastReservedId")
        }
        if (lastReservedId == null) {
            if (logger.isTraceEnabled) {
                logger.trace("No ID for $idType, inserting new row")
            }
            val insertSql = jdbcClient.sql("INSERT INTO id (idKey, lastReservedId) VALUES (:idKey, 0)")
                .param("idKey", idType.toString())
            val insertedRowCount = insertSql.update()
            if (insertedRowCount == 0) {
                throw RuntimeException("No rows inserted for $idType")
            }
            lastReservedId =
                getLastReservedId(idType) ?: throw RuntimeException("Could not set up ID range for $idType")
            if (logger.isTraceEnabled) {
                logger.trace("Second attempt ID get for $idType retrieved $lastReservedId")
            }
        }
        advanceReservedId(idType, blockSize)
        val nextReservedId =
            getLastReservedId(idType) ?: throw RuntimeException("Could not get new ID range for $idType")
        if (logger.isTraceEnabled) {
            logger.trace("Issuing id $lastReservedId + 1 for  $idType")
        }

        return IdDao.ReservedBlock(lastReservedId + 1, nextReservedId)
    }

    private fun getLastReservedId(idType: IdDao.IdType): Long? {
        val queryStatement = jdbcClient.sql(
            "SELECT idKey, lastReservedId FROM id WHERE idKey = :idKey FOR UPDATE"
        ).param("idKey", idType.toString())
        val rowMapper = IdRowMapper()
        val results = queryStatement.query(rowMapper).list()
        if (results.size > 1) {
            throw java.lang.RuntimeException("Retrieved ${results.size} rows for idType $idType, instead of just 1")
        }
        return if (results.size > 0) results.first()?.idVal else null
    }

    private fun advanceReservedId(idType: IdDao.IdType, blockSize: Int) {
        val insertSql =
            jdbcClient.sql("UPDATE ID SET LASTRESERVEDID = LASTRESERVEDID + :increment WHERE IDKEY = :idKey")
                .param("idKey", idType.toString())
                .param("increment", blockSize)
        val updatedRowCount = insertSql.update()
        if (updatedRowCount == 0) {
            throw RuntimeException("No rows updated for $idType")
        }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(IdDaoJdbcImpl::class.java)
    }
}