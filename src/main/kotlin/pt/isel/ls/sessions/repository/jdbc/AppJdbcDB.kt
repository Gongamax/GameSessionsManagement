package pt.isel.ls.sessions.repository.jdbc

import kotlinx.datetime.Clock
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.ls.sessions.repository.AppDB
import pt.isel.ls.sessions.repository.GameRepository
import pt.isel.ls.sessions.repository.PlayerRepository
import pt.isel.ls.sessions.repository.SessionRepository
import pt.isel.ls.sessions.repository.data.game.GameMemoryDB
import pt.isel.ls.sessions.repository.jdbc.game.GameJDBC
import pt.isel.ls.sessions.repository.jdbc.player.PlayerJDBC
import pt.isel.ls.sessions.repository.jdbc.session.SessionJDBC

class AppJdbcDB(
    private val jdbcDatabaseURL: String,
    private val clock: Clock
) : AppDB {
    private val dataSource = PGSimpleDataSource().apply {
        setURL(jdbcDatabaseURL)
    }

    override val playerDB: PlayerRepository
        get() = PlayerJDBC(dataSource)
    override val sessionDB: SessionRepository
        get() = SessionJDBC(dataSource, clock)
    override val gameDB: GameRepository
        get() = GameJDBC(dataSource)
}