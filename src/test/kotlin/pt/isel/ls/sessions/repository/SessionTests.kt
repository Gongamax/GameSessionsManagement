package pt.isel.ls.sessions.repository

import kotlinx.datetime.LocalDateTime
import pt.isel.ls.sessions.repository.data.AppMemoryDB
import kotlin.test.*

//TODO: ADD MORE TESTS
class SessionTests {

    @Test
    fun `session creation with valid params`() {
        // Arrange
        val db = AppMemoryDB()
        val capacity = 10
        val gid = 1
        val date = LocalDateTime(2035, 1, 1, 0, 0, 0, 0)

        // Act
        db.gameMemoryDB.createGame("game1", "game1", emptyList())
        val sid = db.sessionMemoryDB.createSession(capacity, gid.toUInt(), date)

        // Assert
        assertNotNull(sid)
    }

    @Test
    fun `create and get a session`() {
        // Arrange
        val db = AppMemoryDB()
        val capacity = 10
        val gid = 1
        val date = LocalDateTime(2035, 1, 1, 0, 0, 0, 0)

        // Act
        db.gameMemoryDB.createGame("game1", "game1", emptyList())
        val sid = db.sessionMemoryDB.createSession(capacity, gid.toUInt(), date)
        val session = db.sessionMemoryDB.getSession(sid)

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
        val db = AppMemoryDB()
        val capacity = 10
        val gid = 1
        val date = LocalDateTime(2035, 1, 1, 0, 0, 0, 0)

        // Act
        db.gameMemoryDB.createGame("game1", "game1", emptyList())
        val sid = db.sessionMemoryDB.createSession(capacity, gid.toUInt(), date)
        val sessions = db.sessionMemoryDB.getSessions(gid.toUInt(), null, null, null)

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
        val db = AppMemoryDB()
        val capacity = 10
        val gid = 1
        val date = LocalDateTime(2035, 1, 1, 0, 0, 0, 0)
        val pid : UInt = 1u

        // Act
        db.gameMemoryDB.createGame("game1", "game1", emptyList())
        val sid = db.sessionMemoryDB.createSession(capacity, gid.toUInt(), date)
        db.playerMemoryDB.createPlayer("player1", "player1@isel.pt")
        db.sessionMemoryDB.addPlayerToSession(sid, db.playerMemoryDB.getPlayerById(pid)!!)

        // Assert
        val session = db.sessionMemoryDB.getSession(sid)
        assertNotNull(session)
        assertEquals(1, session.associatedPlayers.size)
        assertEquals(pid, session.associatedPlayers.first().pid)
    }
}