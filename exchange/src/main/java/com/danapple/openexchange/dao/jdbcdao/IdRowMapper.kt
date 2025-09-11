package com.danapple.openexchange.dao.jdbcdao

import com.danapple.openexchange.dao.IdDao
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class IdRowMapper : RowMapper<IdRow> {
    override fun mapRow(rs: ResultSet, rowNum: Int): IdRow {
        val idKey = IdDao.IdType.valueOf(rs.getString("idKey"))
        val idVal = rs.getLong("lastReservedId")
        return IdRow(idKey, idVal)
    }
}
