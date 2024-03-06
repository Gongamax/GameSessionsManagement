package pt.isel.ls.sessions.services

import kotlinx.datetime.LocalDateTime
import pt.isel.ls.sessions.repository.data.AppMemoryDB
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

//TODO: ADD MORE TESTS
class SessionTests {

    @Test
    fun `session creation with valid params`() {
        // Arrange
        val db = AppMemoryDB()
        val service = AppService(db)
        val capacity = 10
        val gid = 1
        val date = LocalDateTime(2035, 1, 1, 0, 0, 0, 0)

        // Act
        db.gameMemoryDB.createGame("game1", "game1", emptyList())
        val sid = service.sessionService.createSession(capacity, gid, date)

        // Assert
        assertNotNull(sid)
    }

    @Test
    fun `create and get a session`() {
        // Arrange
        val db = AppMemoryDB()
        val service = AppService(db)
        val capacity = 10
        val gid = 1
        val date = LocalDateTime(2035, 1, 1, 0, 0, 0, 0)

        // Act
        db.gameMemoryDB.createGame("game1", "game1", emptyList())
        val sid = service.sessionService.createSession(capacity, gid, date)
        val session = service.sessionService.getSession(sid)

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
        val db = AppMemoryDB()
        val service = AppService(db)
        val capacity = 10
        val gid = 1
        val date = LocalDateTime(2035, 1, 1, 0, 0, 0, 0)

        // Act
        db.gameMemoryDB.createGame("game1", "game1", emptyList())
        val sid = service.sessionService.createSession(capacity, gid, date)
        val sessions = service.sessionService.getSessions(gid)

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
        val db = AppMemoryDB()
        val service = AppService(db)
        val capacity = 10
        val gid = 1
        val date = LocalDateTime(2035, 1, 1, 0, 0, 0, 0)

        // Act
        db.gameMemoryDB.createGame("game1", "game1", emptyList())
        val sid = service.sessionService.createSession(capacity, gid, date)
        val sessions = service.sessionService.getSessions(gid, date)

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
        val db = AppMemoryDB()
        val service = AppService(db)
        val capacity = 10
        val gid = 1
        val date = LocalDateTime(2035, 1, 1, 0, 0, 0, 0)

        // Act
        db.gameMemoryDB.createGame("game1", "game1", emptyList())
        val sid = service.sessionService.createSession(capacity, gid, date)
        db.playerMemoryDB.createPlayer("player1", "player1@isel.pt")
        val pid = db.playerMemoryDB.getPlayers().first().pid
        service.sessionService.addPlayerToSession(sid, pid)
        val session = service.sessionService.getSession(sid)

        // Assert
        assertNotNull(session)
        assertEquals(1, session.associatedPlayers.size)
        assertEquals(pid, session.associatedPlayers.first().pid)
    }
}