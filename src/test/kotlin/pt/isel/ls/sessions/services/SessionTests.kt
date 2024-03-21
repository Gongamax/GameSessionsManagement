package pt.isel.ls.sessions.services

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import pt.isel.ls.sessions.domain.game.Genres
import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.utils.Failure
import pt.isel.ls.utils.Success
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

//TODO: ADD MORE TESTS
class SessionTests {

    private val clock = Clock.System

    @Test
    fun `session creation with valid params`() {
        // Arrange
        val db = AppMemoryDB(clock)
        val service = AppService(db, clock)
        val capacity = 10
        val gid = 1u
        val date = LocalDateTime(2035, 1, 1, 0, 0, 0, 0)

        // Act
        db.gameDB.createGame("game1", "game1", GENRES)
        val sid = service.sessionService.createSession(capacity, gid, date)

        // Assert
        assertNotNull(sid)
    }

    @Test
    fun `create and get a session`() {
        // Arrange
        val db = AppMemoryDB(clock)
        val service = AppService(db, clock)
        val capacity = 10
        val gid = 1u
        val date = LocalDateTime(2035, 1, 1, 0, 0, 0, 0)

        // Act
        db.gameDB.createGame("game1", "game1", GENRES)
        val sessionId = when (val res = service.sessionService.createSession(capacity, gid, date)) {
            is Success -> res.value
            else -> null
        }
        val sid = sessionId ?: return
        val session = when (val res = service.sessionService.getSession(sid)) {
            is Success -> res.value
            is Failure -> null
        }

        // Assert
        assertNotNull(session)
        assertEquals(sid, session.sid)
        assertEquals(capacity, session.capacity)
        assertEquals(gid, session.gid)
        assertEquals(date, session.date)
    }

    @Test
    fun `create and get all sessions`() {
        // Arrange
        val db = AppMemoryDB(clock)
        val service = AppService(db, clock)
        val capacity = 10
        val gid = 1u
        val date = LocalDateTime(2035, 1, 1, 0, 0, 0, 0)

        // Act
        db.gameDB.createGame("game1", "game1", GENRES)
        val sid = when (val res = service.sessionService.createSession(capacity, gid, date)) {
            is Success -> res.value
            is Failure -> null
        }
        val sessions = when (val res = service.sessionService.getSessions(gid)) {
            is Success -> res.value
            is Failure -> null
        }

        // Assert
        assertNotNull(sessions)
        assertEquals(1, sessions.size)
        assertEquals(sid, sessions[0].sid)
        assertEquals(capacity, sessions[0].capacity)
        assertEquals(gid, sessions[0].gid)
        assertEquals(date, sessions[0].date)
    }

    @Test
    fun `create and get all sessions with date`() {
        // Arrange
        val db = AppMemoryDB(clock)
        val service = AppService(db, clock)
        val capacity = 10
        val gid = 1u
        val date = LocalDateTime(2035, 1, 1, 0, 0, 0, 0)

        // Act
        db.gameDB.createGame("game1", "game1", GENRES)
        val sid = when (val res = service.sessionService.createSession(capacity, gid, date)) {
            is Success -> res.value
            is Failure -> null
        }
        val sessions = when (val res = service.sessionService.getSessions(gid, date)) {
            is Success -> res.value
            is Failure -> null
        }

        // Assert
        assertNotNull(sessions)
        assertEquals(1, sessions.size)
        assertEquals(sid, sessions[0].sid)
        assertEquals(capacity, sessions[0].capacity)
        assertEquals(gid, sessions[0].gid)
        assertEquals(date, sessions[0].date)
    }

    @Test
    fun `create a session and add player to session`() {
        // Arrange
        val db = AppMemoryDB(clock)
        val service = AppService(db, clock)
        val capacity = 10
        val gid = 1u
        val date = LocalDateTime(2035, 1, 1, 0, 0, 0, 0)

        // Act
        db.gameDB.createGame("game1", "game1", GENRES)
        val sid = when (val res = service.sessionService.createSession(capacity, gid, date)) {
            is Success -> res.value
            is Failure -> null
        }
        assertNotNull(sid, "sid is null")
        db.playerDB.createPlayer("player1", "player1@isel.pt")
        val pid = db.playerDB.getPlayers().first().pid

        service.sessionService.addPlayerToSession(sid, pid)
        val session = when (val res = service.sessionService.getSession(sid)) {
            is Success -> res.value
            is Failure -> null
        }

        // Assert
        assertNotNull(session)
        assertEquals(1, session.associatedPlayers.size)
        assertEquals(pid, session.associatedPlayers.first().pid)
    }

    companion object {
        private val GENRES = listOf(Genres.RPG, Genres.ADVENTURE)
    }
}