package pt.isel.ls.sessions.repository.jdbc.player

import org.slf4j.LoggerFactory
import pt.isel.ls.sessions.domain.player.Email
import pt.isel.ls.sessions.domain.player.Player
import pt.isel.ls.sessions.domain.utils.Token
import pt.isel.ls.sessions.repository.PlayerRepository
import pt.isel.ls.sessions.repository.jdbc.execute
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.*
import javax.sql.DataSource

class PlayerJDBC(
    private val dataSource: DataSource,
) : PlayerRepository {
    override fun createPlayer(
        name: String,
        email: String,
    ): Token {
        return dataSource.connection.execute("Failed to create player") { con ->
            val token = UUID.randomUUID()
            val query = "INSERT INTO Player (name, email, token) VALUES (?, ?, ?)".trimIndent()
            val stm =
                con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS).apply {
                    setString(1, name)
                    setString(2, email)
                    setObject(3, token)
                }
            if (stm.executeUpdate() == 0) {
                throw SQLException("Creating player failed, no rows affected.")
            }
            val generatedKeys = stm.generatedKeys
            if (generatedKeys.next()) {
                return@execute Token(generatedKeys.getInt(1).toUInt(), token)
            } else {
                throw SQLException("Creating player failed, no ID obtained.")
            }
        }
    }

    override fun getPlayers(): List<Player> =
        dataSource.connection.execute("Failed to get players") { con ->
            val query = "SELECT * FROM Player"
            val resultSet = con.prepareStatement(query).executeQuery()
            val players = mutableListOf<Player>()
            while (resultSet.next()) {
                players.add(resultSet.toPlayer())
            }
            return players
        }

    override fun getPlayerById(pid: UInt): Player? =
        dataSource.connection.execute("Failed to get player by id") { con ->
            val query = "SELECT * FROM Player WHERE id = ?"
            val rs =
                con.prepareStatement(query).apply {
                    setInt(1, pid.toInt())
                }.executeQuery()
            if (rs.next()) rs.toPlayer() else null
        }

    override fun reset() {
        return dataSource.connection.execute("Failed to reset players") { con ->
            val query = "DELETE FROM Player"
            con.prepareStatement(query).executeUpdate()
        }
    }

    override fun isEmailInUse(email: String): Boolean =
        dataSource.connection.execute("Failed to check if email is in use") { con ->
            val query = "SELECT * FROM Player WHERE email = ?".trimIndent()
            con.prepareStatement(query).apply {
                setString(1, email)
            }.executeQuery().next()
        }

    override fun isNameInUse(name: String): Boolean {
        return dataSource.connection.execute("Failed to check if name is in use") { con ->
            val query = "SELECT * FROM Player WHERE name = ?".trimIndent()
            con.prepareStatement(query).apply {
                setString(1, name)
            }.executeQuery().next()
        }
    }

    override fun searchPlayers(
        name: String,
        limit: Int,
        skip: Int,
    ): List<Player> =
        dataSource.connection.execute("Failed to search players") { con ->
            val query = "SELECT * FROM Player WHERE name LIKE ? LIMIT ? OFFSET ?"
            val stm =
                con.prepareStatement(query).apply {
                    setString(1, "%$name%")
                    setInt(2, limit)
                    setInt(3, skip)
                }
            val rs = stm.executeQuery()
            val players = mutableListOf<Player>()
            while (rs.next()) {
                players.add(rs.toPlayer())
            }
            return players
        }

    private fun ResultSet.toPlayer(): Player {
        val id = getInt("id").toUInt()
        val name = getString("name")
        val email = getString("email")
        return Player(id, name, Email(email))
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PlayerJDBC::class.java)
    }
}
