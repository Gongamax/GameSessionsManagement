package pt.isel.ls.sessions.repository.jdbc.session

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toLocalDateTime
import pt.isel.ls.sessions.domain.player.Email
import pt.isel.ls.sessions.domain.player.Player
import pt.isel.ls.sessions.domain.session.Session
import pt.isel.ls.sessions.domain.session.SessionState
import pt.isel.ls.sessions.repository.SessionRepository
import pt.isel.ls.sessions.repository.jdbc.execute
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.sql.Timestamp
import javax.sql.DataSource

typealias AssociatedPlayers = Set<Player>

class SessionJDBC(
    private val dataSource: DataSource,
    private val clock: Clock = Clock.System,
) : SessionRepository {
    override fun createSession(
        capacity: Int,
        gid: UInt,
        date: LocalDateTime,
    ): UInt =
        dataSource.connection.execute("Session not created") { con ->
            val query = "INSERT INTO Session (date, game, capacity) VALUES (?, ?, ?)"
            val stm =
                con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS).apply {
                    setTimestamp(1, Timestamp.valueOf(date.toJavaLocalDateTime()))
                    setInt(2, gid.toInt())
                    setInt(3, capacity)
                }
            if (stm.executeUpdate() == 0) {
                throw SQLException("Creating session failed, no rows affected.")
            }
            val generatedKeys = stm.generatedKeys
            return@execute if (generatedKeys.next()) {
                generatedKeys.getInt(1).toUInt()
            } else {
                throw SQLException("Creating session failed, no ID obtained.")
            }
        }

    override fun addPlayerToSession(
        sid: UInt,
        player: Player,
    ) = dataSource.connection.execute("Player not added to session") { con ->
        val query = "INSERT INTO Session_Player (session_id, player_id) VALUES (?, ?)"
        con.prepareStatement(query).apply {
            setInt(1, sid.toInt())
            setInt(2, player.pid.toInt())
        }.executeUpdate()
        return@execute
    }

    override fun getSession(sid: UInt): Session? =
        dataSource.connection.execute("Session not found") { con ->
            val query = "SELECT * FROM Session WHERE id = ?"
            val rs =
                con.prepareStatement(query).apply {
                    setInt(1, sid.toInt())
                }.executeQuery()
            return@execute if (rs.next()) rs.toSession() else null
        }

    override fun getSessions(
        gid: UInt,
        date: LocalDateTime?,
        state: SessionState?,
        pid: UInt?,
        limit: Int,
        skip: Int,
    ): List<Session> =
        dataSource.connection.execute("Sessions not found") { con ->
            val query =
                """
                SELECT * FROM Session
                WHERE game = ?
                ${if (date != null) "AND date = ?" else ""}
                ${if (pid != null) "AND id IN (SELECT session_id FROM Session_Player WHERE player_id = ?)" else ""}
                ${"LIMIT ? OFFSET ?"}
                """.trimIndent()
            val stm =
                con.prepareStatement(query).apply {
                    var index = 1
                    setInt(index++, gid.toInt())
                    if (date != null) {
                        setTimestamp(index++, Timestamp.valueOf(date.toJavaLocalDateTime()))
                    }
                    if (pid != null) {
                        setInt(index++, pid.toInt())
                    }
                    setInt(index++, limit)
                    setInt(index, skip)
                }
            val rs = stm.executeQuery()
            val sessions = mutableListOf<Session>()
            while (rs.next()) {
                val session = rs.toSession()
                println("session: $session")
                val sessionState = getSessionState(session.date, session.associatedPlayers, session.capacity)
                println("state: $state, sessionState: $sessionState")
                if (state == null || sessionState == state) {
                    sessions.add(session)
                }
            }
            return@execute sessions
        }

    override fun reset() {
        return dataSource.connection.execute("Failed to reset sessions") { con ->
            val query = "DELETE FROM Session"
            con.prepareStatement(query).executeUpdate()
        }
    }

    override fun deleteSession(sid: UInt) {
        return dataSource.connection.execute("Failed to delete session") { con ->
            val query = "DELETE FROM Session WHERE id = ?"
            con.prepareStatement(query).apply {
                setInt(1, sid.toInt())
            }.executeUpdate()
        }
    }

    override fun updateSession(
        sid: UInt,
        capacity: Int,
        date: LocalDateTime,
    ) {
        return dataSource.connection.execute("Failed to update session") { con ->
            val query = "UPDATE Session SET capacity = ?, date = ? WHERE id = ?"
            con.prepareStatement(query).apply {
                setInt(1, capacity)
                setTimestamp(2, Timestamp.valueOf(date.toJavaLocalDateTime()))
                setInt(3, sid.toInt())
            }.executeUpdate()
            return@execute
        }
    }

    override fun removePlayerFromSession(
        sid: UInt,
        pid: UInt,
    ) {
        return dataSource.connection.execute("Failed to remove player from session") { con ->
            val query = "DELETE FROM Session_Player WHERE session_id = ? AND player_id = ?"
            con.prepareStatement(query).apply {
                setInt(1, sid.toInt())
                setInt(2, pid.toInt())
            }.executeUpdate()
        }
    }

    private fun UInt.getAssociatedPlayers(): AssociatedPlayers =
        dataSource.connection.execute("Players not found") { con ->
            val query = "SELECT * FROM Player WHERE id IN (SELECT player_id FROM Session_Player WHERE session_id = ?)"
            val rs =
                con.prepareStatement(query).apply {
                    setInt(1, this@getAssociatedPlayers.toInt())
                }.executeQuery()
            val players = mutableSetOf<Player>()
            while (rs.next())
                players.add(rs.toPlayer())
            return@execute players
        }

    private fun ResultSet.toSession(): Session {
        val id = getInt("id").toUInt()
        val players = id.getAssociatedPlayers()
        val date = getTimestamp("date").toLocalDateTime().toKotlinLocalDateTime()
        val game = getInt("game").toUInt()
        val capacity = getInt("capacity")
        return Session(id, players.size, date, game, players, capacity)
    }

    private fun ResultSet.toPlayer(): Player {
        val id = getInt("id").toUInt()
        val name = getString("name")
        val email = getString("email")
        return Player(id, name, Email(email))
    }

    private fun getSessionState(
        date: LocalDateTime,
        players: Set<Player>,
        capacity: Int,
    ): SessionState {
        val now = clock.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return when {
            players.size < capacity -> SessionState.OPEN
            date < now -> SessionState.OPEN
            else -> SessionState.CLOSED
        }
    }
}
