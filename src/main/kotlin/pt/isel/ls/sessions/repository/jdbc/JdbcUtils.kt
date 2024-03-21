package pt.isel.ls.sessions.repository.jdbc

import java.sql.Connection
import java.sql.ResultSet
import java.sql.Types
import javax.sql.DataSource

data class ExecuteSqlException(val errorInfo: String) : Exception()

// TODO: CREATE CUSTOM EXCEPTION
inline fun <T> Connection.execute(
    errorInfo: String,
    block: (con: Connection) -> T,
): T =
    try {
        autoCommit = false
        block(this).also { commit() }
    } catch (e: Exception) {
        rollback()
        throw ExecuteSqlException(errorInfo)
    } finally {
        close()
    }

typealias Mapper<T> = (ResultSet) -> T

fun <T> DataSource.query(
    sql: String,
    vararg params: Any,
    mapper: Mapper<T>,
): List<T> {
    return this.connection.use { c ->
        c.prepareStatement(sql).use { ps ->
            for ((index, param) in params.withIndex()) {
                ps.setObject(index + 1, param)
            }
            ps.executeQuery().use { rs ->
                val result = mutableListOf<T>()
                while (rs.next()) {
                    result.add(mapper(rs))
                }
                result
            }
        }
    }
}

fun DataSource.update(
    sql: String,
    vararg params: Any?,
): Int {
    return this.connection.use { c ->
        c.prepareStatement(sql).use { ps ->
            params.withIndex().forEach { (index, param) ->
                if (param == null) {
                    ps.setNull(index + 1, Types.NULL)
                } else {
                    ps.setObject(index + 1, param)
                }
            }
            ps.executeUpdate()
        }
    }
}
