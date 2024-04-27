package pt.isel.ls.sessions.repository.jdbc.game

import pt.isel.ls.sessions.domain.game.Game
import pt.isel.ls.sessions.domain.game.Genres
import pt.isel.ls.sessions.repository.GameRepository
import pt.isel.ls.sessions.repository.jdbc.execute
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import javax.sql.DataSource

class GameJDBC(
    private val dataSource: DataSource,
) : GameRepository {
    override fun createGame(
        name: String,
        developer: String,
        genres: List<Genres>,
    ): UInt =
        dataSource.connection.execute("Failed to create game") { con ->
            val query = "INSERT INTO Game (name, developer, genres) VALUES (?, ?, ?)".trimIndent()
            val genreOrdinals = getGenreOrdinals(genres)
            val stm =
                con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS).apply {
                    setString(1, name)
                    setString(2, developer)
                    setArray(3, con.createArrayOf("integer", genreOrdinals))
                }
            if (stm.executeUpdate() == 0) {
                throw SQLException("Failed to create game")
            }
            val generatedKeys = stm.generatedKeys
            return@execute if (generatedKeys.next()) {
                generatedKeys.getInt(1).toUInt()
            } else {
                throw SQLException("Failed to create game")
            }
        }

    override fun getGames(
        genres: List<Genres>,
        developer: String,
        limit: Int,
        skip: Int,
    ): List<Game> =
        dataSource.connection.execute("Failed to get games") { con ->
            val query =
                if (developer.isNotBlank() || genres.isNotEmpty()) {
                    """
                    SELECT * FROM Game 
                    WHERE ${if (developer.isNotBlank()) "developer = ? " else ""}  
                    ${if (developer.isNotBlank() && genres.isNotEmpty()) "AND " else ""}
                    ${if (genres.isNotEmpty()) "genres @> ?" else ""} 
                    LIMIT ? OFFSET ?
                    """.trimIndent()
                } else {
                    "SELECT * FROM Game LIMIT ? OFFSET ?"
                }

            val genreOrdinals = getGenreOrdinals(genres)
            val stm =
                con.prepareStatement(query).apply {
                    var index = 1
                    if (developer.isNotBlank()) setString(index++, developer)
                    if (genreOrdinals.isNotEmpty()) setArray(index++, con.createArrayOf("integer", genreOrdinals))
                    setInt(index++, limit)
                    setInt(index, skip)
                }
            val rs = stm.executeQuery()
            val games = mutableListOf<Game>()
            while (rs.next())
                games.add(getGameResponse(rs))
            return@execute games
        }

    override fun getGameById(gid: UInt): Game? =
        dataSource.connection.execute("Failed to get game") { con ->
            val query = "SELECT * FROM Game WHERE id = ?"
            val stm =
                con.prepareStatement(query).apply {
                    setInt(1, gid.toInt())
                }
            val rs = stm.executeQuery()
            return@execute if (rs.next()) getGameResponse(rs) else null
        }

    override fun reset() =
        dataSource.connection.execute("Failed to reset") { con ->
            val query = "DELETE FROM Game"
            val stm = con.prepareStatement(query)
            stm.executeUpdate()
            return@execute
        }

    override fun getDeveloperByName(developer: String): String? =
        dataSource.connection.execute("Failed to get developer") { con ->
            val query = "SELECT developer FROM Game WHERE developer = ? LIMIT 1"
            val stm =
                con.prepareStatement(query).apply {
                    setString(1, developer)
                }
            val rs = stm.executeQuery()
            return@execute if (rs.next()) rs.getString("developer") else null
        }

    override fun getGameByName(name: String): Boolean =
        dataSource.connection.execute("Failed to get game") { con ->
            val query = "SELECT name FROM GAME where name = ?"
            val stm =
                con.prepareStatement(query).apply {
                    setString(1, name)
                }
            val rs = stm.executeQuery()
            return@execute rs.next()
        }

    private fun getGameResponse(rs: ResultSet): Game {
        val id = rs.getInt("id").toUInt()
        val name = rs.getString("name")
        val dev = rs.getString("developer")
        val genreOrdinals = (rs.getArray("genres").array as Array<*>)
        val gen = genreOrdinals.map { Genres.entries[it as Int] }
        return Game(id, name, dev, gen)
    }

    private fun getGenreOrdinals(genres: List<Genres>) = genres.map { it.ordinal }.toTypedArray()
}
