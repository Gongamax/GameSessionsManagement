package pt.isel.ls.sessions.http

import junit.framework.TestCase.assertEquals
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.junit.Test
import pt.isel.ls.sessions.domain.game.toGenre
import pt.isel.ls.sessions.http.model.player.PlayerDTO
import pt.isel.ls.sessions.http.model.session.SessionCreateDTO
import pt.isel.ls.sessions.http.model.session.SessionDTO
import pt.isel.ls.sessions.http.model.utils.MessageResponse
import pt.isel.ls.sessions.http.routes.player.PlayerRouter
import pt.isel.ls.sessions.http.routes.session.SessionRouter
import pt.isel.ls.sessions.http.util.Uris
import pt.isel.ls.sessions.repository.data.game.GameMemoryDB
import pt.isel.ls.sessions.repository.data.player.PlayerMemoryDB
import pt.isel.ls.sessions.repository.data.session.SessionMemoryDB
import pt.isel.ls.sessions.services.player.PlayerService
import pt.isel.ls.sessions.services.session.SessionService
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SessionTests {
    companion object {
        private val clock: Clock = Clock.System
        private val sessionDB = SessionMemoryDB(clock)
        private val playerDB = PlayerMemoryDB()
        private val gameDB = GameMemoryDB()
        private val sessionService = SessionService(sessionDB, playerDB, gameDB, clock)
        private val sessionRouter = SessionRouter(sessionService)
    }

    @BeforeTest
    fun createGame() {
        val name = "FIFA"
        val developer = "Francisco "
        val genres = listOf("Sports", "MultiPlayer").mapNotNull { it.toGenre() }
        gameDB.createGame(name, developer, genres)
    }

    @AfterTest
    fun cleanGame() {
        gameDB.reset()
    }

    private fun createSession() {
        val capacity = 10
        val gid = 1u
        val date = "2030-03-14T10:58:00.123456789"
        val request =
            Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(SessionCreateDTO(gid, date, capacity)))
        sessionRouter.routes(request)
    }

    private fun createPlayer() {
        val name = "Francisco"
        val email = "francisco@gmail.com"
        val createPlayer = Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(PlayerDTO(name, email)))
        PlayerRouter(PlayerService(playerDB)).routes(createPlayer)
    }

    @Test
    fun `create return response with a session`() {
        // Arrange
        val capacity = 10
        val gid = 1u
        val date = "2025-03-14T10:58:00.123456789"
        val request =
            Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(SessionCreateDTO(gid, date, capacity)))
        // Act
        println(request)
        val response = sessionRouter.routes(request)
        // Assert
        assertTrue { response.header("Location") != null }
        val sid = response.header("Location")?.split("/")?.last()
        assertEquals(expected = "/sessions/$sid", actual = response.header("Location"))
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        assertEquals(expected = 201, actual = response.status.code)
        assertEquals(expected = "Session created: $sid", actual = content.message)
    }

    @Test
    fun `create return response with a session Game not found`() {
        // Arrange
        val capacity = 10
        val gid = 2u
        val date = "2025-03-14T10:58:00.123456789"
        val request =
            Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(SessionCreateDTO(gid, date, capacity)))
        // Act
        val response = sessionRouter.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        assertEquals(expected = 404, actual = response.status.code)
        assertEquals(expected = "Game not found", actual = content.message)
    }

    @Test
    fun `create return response with a session Invalid date`() {
        // Arrange
        val capacity = 10
        val gid = 1u
        val date = "2020-03-14T10:58:00.123456789"
        val request =
            Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(SessionCreateDTO(gid, date, capacity)))
        // Act
        val response = sessionRouter.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        assertEquals(expected = 400, actual = response.status.code)
        assertEquals(expected = "Invalid date", actual = content.message)
    }

    @Test
    fun `create return response with a session Invalid capacity`() {
        // Arrange
        val capacity = 0
        val gid = 1u
        val date = "2025-03-14T10:58:00.123456789"
        val request =
            Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(SessionCreateDTO(gid, date, capacity)))
        // Act
        val response = sessionRouter.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        assertEquals(expected = 400, actual = response.status.code)
        assertEquals(expected = "Invalid capacity", actual = content.message)
    }

    @Test
    fun `getSession return response with a session`() {
        // Arrange
        createSession()
        val gid = 1u
        val request = Request(Method.GET, Uris.Sessions.GET_BY_ID.replace("{sid}", "1"))

        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<SessionDTO>(response.bodyString())
        // Assert
        assertEquals(expected = 200, actual = response.status.code)
        assertEquals(expected = "application/json", actual = response.header("Content-Type"))
        assertEquals(expected = 1u, actual = content.sid)
        assertEquals(expected = gid, actual = content.gid)
        assertEquals(expected = 10, actual = content.capacity)
        assertEquals("2030-03-14T10:58:00.123456789", content.date.toString())
    }

    @Test
    fun `getSession return response with a session not found`() {
        // Arrange
        val request = Request(Method.GET, Uris.Sessions.GET_BY_ID.replace("{sid}", "99"))
        // Act
        val response = sessionRouter.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        assertEquals(expected = 404, actual = response.status.code)
        assertEquals(expected = "Session not found", actual = content.message)
    }

    @Test
    fun `getSessions return response with a session`() {
        // Arrange
        createSession()
        val request = Request(Method.GET, Uris.DEFAULT).query("gid", "1")
        // Act
        val response = sessionRouter.routes(request)
        // Assert
        val content = Json.decodeFromString<List<SessionDTO>>(response.bodyString())
        assertEquals(expected = 200, actual = response.status.code)
        assertEquals(expected = "application/json", actual = response.header("Content-Type"))
        assertEquals(expected = 1u, actual = content[0].sid)
        assertEquals(expected = 1u, actual = content[0].gid)
        assertEquals(expected = 10, actual = content[0].capacity)
        assertEquals("2030-03-14T10:58:00.123456789", content[0].date.toString())
    }

    @Test
    fun `getSessions return response with a session State OPEN`() {
        // Arrange
        createSession()
        val request = Request(Method.GET, Uris.DEFAULT).query("gid", "1").query("state", "OPEN")
        // Act
        val response = sessionRouter.routes(request)
        // Assert
        val content = Json.decodeFromString<List<SessionDTO>>(response.bodyString())
        assertEquals(expected = 200, actual = response.status.code)
        assertEquals(expected = "application/json", actual = response.header("Content-Type"))
        assertEquals(expected = 1u, actual = content[0].sid)
        assertEquals(expected = 1u, actual = content[0].gid)
        assertEquals(expected = 10, actual = content[0].capacity)
        assertEquals("2030-03-14T10:58:00.123456789", content[0].date.toString())
    }

    @Test
    fun `getSessions return response with a session Game not found`() {
        // Arrange
        createGame()
        val request = Request(Method.GET, Uris.DEFAULT).query("gid", "2")
        // Act
        val response = sessionRouter.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        assertEquals(expected = 404, actual = response.status.code)
        assertEquals(expected = "Game not found", actual = content.message)
    }

    @Test
    fun `getSessions return response with a session Invalid date`() {
        // Arrange
        createSession()
        val request = Request(Method.GET, Uris.DEFAULT).query("gid", "1").query("date", "2020-03-14T10:58:00.123456789")
        // Act
        val response = sessionRouter.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        assertEquals(expected = 400, actual = response.status.code)
        assertEquals(expected = "Invalid date", actual = content.message)
    }

    @Test
    fun `getSessions return response with a session Invalid state`() {
        // Arrange
        createSession()
        val request = Request(Method.GET, Uris.Sessions.ROOT).query("gid", "1").query("state", "OPES")
        // Act
        val response = sessionRouter.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        assertEquals(expected = 400, actual = response.status.code)
        assertEquals(expected = "Invalid request, invalid number format", actual = content.message)
    }

    @Test
    fun `getSessions return response with a session Player not found`() {
        // Arrange
        createSession()
        val request = Request(Method.GET, Uris.DEFAULT).query("gid", "1").query("pid", "99")
        // Act
        val response = sessionRouter.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        assertEquals(expected = 404, actual = response.status.code)
        assertEquals(expected = "Player not found", actual = content.message)
    }

    @Test
    fun `addPlayerToSession return response with a session`() {
        // Arrange
        createSession()
        createPlayer()
        val request = Request(Method.PUT, Uris.Sessions.ADD_PLAYER.replace("{sid}", "1").replace("{pid}", "1"))
        // Act<
        val response = sessionRouter.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        assertEquals(expected = 200, actual = response.status.code)
        assertEquals(expected = "Player added to session", actual = content.message)
    }
}
