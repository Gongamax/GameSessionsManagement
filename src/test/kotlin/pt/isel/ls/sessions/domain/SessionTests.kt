package pt.isel.ls.sessions.domain

import kotlinx.datetime.LocalDateTime
import pt.isel.ls.sessions.domain.session.Session
import kotlin.test.Test
import kotlin.test.assertEquals

class SessionTests {

    @Test
    fun `test session creation`() {
        // Arrange
        val localDateTime = LocalDateTime(2021, 1, 1, 0, 0)
        val session = Session(1, 1, localDateTime, 1, emptySet(), 5)
        // Act
        // Assert
        assertEquals(1, session.sid)
        assertEquals(1, session.numberOfPlayers)
        assertEquals(1, session.gid)
        assertEquals(localDateTime, session.date)
    }

    @Test
    fun `test session toString`() {
        // Arrange
        val localDateTime = LocalDateTime(2021, 1, 1, 0, 0)
        val session = Session(1, 1, localDateTime, 1, emptySet(), 5)
        // Act
        val result = session.toString()
        // Assert
        assertEquals("Session(sid=1, numberOfPlayers=1, date=2021-01-01T00:00, gid=1, associatedPlayers=[])", result)
    }
}