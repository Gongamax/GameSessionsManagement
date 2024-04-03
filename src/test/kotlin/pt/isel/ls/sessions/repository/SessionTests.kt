package pt.isel.ls.sessions.repository

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import pt.isel.ls.sessions.domain.game.Genres
import pt.isel.ls.sessions.repository.data.AppMemoryDB
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SessionTests {
    private val clock = Clock.System

    @Test
    fun `session creation with valid params`() {
        // Arrange
        val db = AppMemoryDB(clock)
        val capacity = 10
        val gid = 1
        val date = LocalDateTime(2035, 1, 1, 0, 0, 0, 0)

        // Act
        db.gameDB.createGame("game1", "game1", GENRES)
        val sid = db.sessionDB.createSession(capacity, gid.toUInt(), date)

        // Assert
        assertNotNull(sid)
    }

    @Test
    fun `create and get a session`() {
        // Arrange
        val db = AppMemoryDB(clock)
        val capacity = 10
        val gid = 1
        val date = LocalDateTime(2035, 1, 1, 0, 0, 0, 0)

        // Act
        db.gameDB.createGame("game1", "game1", GENRES)
        val sid = db.sessionDB.createSession(capacity, gid.toUInt(), date)
        val session = db.sessionDB.getSession(sid)

        // Assert
        assertNotNull(session)
        assertEquals(sid, session.sid)
        assertEquals(capacity, session.capacity)
        assertEquals(gid.toUInt(), session.gid)
        assertEquals(date, session.date)
    }

    @Test
    fun `create and get all sessions`() {
        // Arrange
        val db = AppMemoryDB(clock)
        val capacity = 10
        val gid = 1
        val date = LocalDateTime(2035, 1, 1, 0, 0, 0, 0)

        // Act
        db.gameDB.createGame("game1", "game1", GENRES)
        val sid = db.sessionDB.createSession(capacity, gid.toUInt(), date)
        val sessions = db.sessionDB.getSessions(gid.toUInt(), null, null, null, DEFAULT_LIMIT, DEFAULT_SKIP)

        // Assert
        assertEquals(1, sessions.size)
        assertEquals(sid, sessions[0].sid)
        assertEquals(capacity, sessions[0].capacity)
        assertEquals(gid.toUInt(), sessions[0].gid)
        assertEquals(date, sessions[0].date)
    }

    @Test
    fun `add player to session`() {
        // Arrange
        val db = AppMemoryDB(clock)
        val capacity = 10
        val gid = 1
        val date = LocalDateTime(2035, 1, 1, 0, 0, 0, 0)
        val pid = 1u

        // Act
        db.gameDB.createGame("game1", "game1", GENRES)
        val sid = db.sessionDB.createSession(capacity, gid.toUInt(), date)
        db.playerDB.createPlayer("player1", "player1@isel.pt")
        db.sessionDB.addPlayerToSession(sid, db.playerDB.getPlayerById(pid)!!)

        // Assert
        val session = db.sessionDB.getSession(sid)
        assertNotNull(session)
        assertEquals(1, session.associatedPlayers.size)
        assertEquals(pid, session.associatedPlayers.first().pid)
    }

    @Test
    fun `delete session`() {
        // Arrange
        val db = AppMemoryDB(clock)
        val capacity = 10
        val gid = 1
        val date = LocalDateTime(2035, 1, 1, 0, 0, 0, 0)

        // Act
        db.gameDB.createGame("game1", "game1", GENRES)
        val sid = db.sessionDB.createSession(capacity, gid.toUInt(), date)
        db.sessionDB.deleteSession(sid)

        // Assert
        val session = db.sessionDB.getSession(sid)
        assertEquals(null, session)
    }

    @Test
    fun `update session`() {
        // Arrange
        val db = AppMemoryDB(clock)
        val capacity = 10
        val gid = 1
        val date = LocalDateTime(2035, 1, 1, 0, 0, 0, 0)
        val newCapacity = 20
        val newDate = LocalDateTime(2025, 1, 2, 0, 0, 0, 0)

        // Act
        db.gameDB.createGame("game1", "game1", GENRES)
        val sid = db.sessionDB.createSession(capacity, gid.toUInt(), date)
        db.sessionDB.updateSession(sid, newCapacity, newDate)

        // Assert
        val session = db.sessionDB.getSession(sid)
        assertNotNull(session)
        assertEquals(newCapacity, session.capacity)
        assertEquals(newDate, session.date)
    }

    @Test
    fun `remove a player from a session`() {
        // Arrange
        val db = AppMemoryDB(clock)
        val capacity = 10
        val gid = 1
        val date = LocalDateTime(2035, 1, 1, 0, 0, 0, 0)
        val pid = 1u

        // Act
        db.gameDB.createGame("game1", "game1", GENRES)
        val sid = db.sessionDB.createSession(capacity, gid.toUInt(), date)
        db.playerDB.createPlayer("player1", "player1@gmail.com")
        db.sessionDB.addPlayerToSession(sid, db.playerDB.getPlayerById(pid)!!)
        db.sessionDB.removePlayerFromSession(sid, pid)

        // Assert
        val session = db.sessionDB.getSession(sid)
        assertNotNull(session)
        assertEquals(0, session.associatedPlayers.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `try to remove a player from a session that does not exist`() {
        // Arrange
        val db = AppMemoryDB(clock)
        val capacity = 10
        val gid = 1
        val date = LocalDateTime(2035, 1, 1, 0, 0, 0, 0)
        val pid = 1u

        // Act
        db.gameDB.createGame("game1", "game1", GENRES)
        val sid = db.sessionDB.createSession(capacity, gid.toUInt(), date)
        db.playerDB.createPlayer("player1", "player1@gmail.com")
        db.sessionDB.addPlayerToSession(sid, db.playerDB.getPlayerById(pid)!!)
        db.sessionDB.removePlayerFromSession(sid + 1u, pid)

        // Assert
        val session = db.sessionDB.getSession(sid)
        assertNotNull(session)
        assertEquals(1, session.associatedPlayers.size)
    }

    companion object {
        private val GENRES = listOf(Genres.RPG, Genres.ADVENTURE)
        private const val DEFAULT_LIMIT = 10
        private const val DEFAULT_SKIP = 0
    }
}
