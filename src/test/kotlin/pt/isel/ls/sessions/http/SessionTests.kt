package pt.isel.ls.sessions.http

import junit.framework.TestCase.assertEquals
import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.Test
import pt.isel.ls.sessions.domain.game.toGenre
import pt.isel.ls.sessions.domain.player.Email
import pt.isel.ls.sessions.http.model.game.GameDTO
import pt.isel.ls.sessions.http.model.player.PlayerDTO
import pt.isel.ls.sessions.http.model.session.SessionCreateDTO
import pt.isel.ls.sessions.http.model.session.SessionDTO
import pt.isel.ls.sessions.http.model.utils.MessageResponse
import pt.isel.ls.sessions.http.routes.player.PlayerRouter
import pt.isel.ls.sessions.http.routes.session.SessionRouter
import pt.isel.ls.sessions.http.util.APPLICATION_JSON
import pt.isel.ls.sessions.http.util.CONTENT_TYPE
import pt.isel.ls.sessions.http.util.Uris
import pt.isel.ls.sessions.repository.data.game.GameMemoryDB
import pt.isel.ls.sessions.repository.data.player.PlayerMemoryDB
import pt.isel.ls.sessions.repository.data.session.SessionMemoryDB
import pt.isel.ls.sessions.services.player.PlayerService
import pt.isel.ls.sessions.services.session.SessionService
import kotlin.test.*

class SessionTests {
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
        val capacity = 5
        val gid = 1u
        val date = "2030-03-14T10:58:00.123456789"
        val request =
            Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(SessionCreateDTO(gid, date, capacity)))
        sessionRouter.routes(request)
    }

    private fun createPlayer(email: String = "francisco@gmail.com") {
        val name = "Francisco"
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
        val response = sessionRouter.routes(request)
        // Assert
        assertTrue { response.header("Location") != null }
        val sid = response.header("Location")?.split("/")?.last()
        assertEquals(expected = "/sessions/$sid", actual = response.header("Location"))
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        assertEquals(expected = Status.CREATED, actual = response.status)
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
        assertEquals(expected = Status.NOT_FOUND, actual = response.status)
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
        assertEquals(expected = Status.BAD_REQUEST, actual = response.status)
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
        assertEquals(expected = Status.BAD_REQUEST, actual = response.status)
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
        assertEquals(expected = Status.OK, actual = response.status)
        assertEquals(expected = "application/json", actual = response.header("Content-Type"))
        assertEquals(expected = 1u, actual = content.sid)
        assertEquals(expected = gid, actual = content.gid)
        assertEquals(expected = 5, actual = content.capacity)
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
        assertEquals(expected = Status.NOT_FOUND, actual = response.status)
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
        assertEquals(expected = Status.OK, actual = response.status)
        assertEquals(expected = "application/json", actual = response.header("Content-Type"))
        assertEquals(expected = 1u, actual = content[0].sid)
        assertEquals(expected = 1u, actual = content[0].gid)
        assertEquals(expected = 5, actual = content[0].capacity)
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
        assertEquals(expected = Status.OK, actual = response.status)
        assertEquals(expected = "application/json", actual = response.header("Content-Type"))
        assertEquals(expected = 1u, actual = content[0].sid)
        assertEquals(expected = 1u, actual = content[0].gid)
        assertEquals(expected = 5, actual = content[0].capacity)
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
        val content = Json.decodeFromString<List<SessionDTO>>(response.bodyString())
        assertTrue { response.status == Status.OK }
        assertTrue { response.header(CONTENT_TYPE) == APPLICATION_JSON }
        assertTrue(content.isEmpty())
        assertTrue(content.isEmpty())
        assertEquals("[]", content.toString())
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
        assertEquals(expected = Status.BAD_REQUEST, actual = response.status)
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
        assertEquals(expected = Status.BAD_REQUEST, actual = response.status)
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
        assertEquals(expected = Status.NOT_FOUND, actual = response.status)
        assertEquals(expected = "Player not found", actual = content.message)
    }

    @Test
    fun `getSessions return response with a session Limit or skip is negative`() {
        // Arrange
        createSession()
        val request = Request(Method.GET, Uris.DEFAULT).query("gid", "1").query("limit", "-1").query("skip", "-1")
        // Act
        val response = sessionRouter.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        assertEquals(expected = Status.BAD_REQUEST, actual = response.status)
        assertEquals(expected = "Limit or skip is negative", actual = content.message)
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
        assertEquals(expected = Status.OK, actual = response.status)
        assertEquals(expected = "Player added to session", actual = content.message)
    }

    @Test
    fun `addPlayerToSession return response with a session Player not found`() {
        // Arrange
        createSession()
        val request = Request(Method.PUT, Uris.Sessions.ADD_PLAYER.replace("{sid}", "1").replace("{pid}", "999"))
        // Act
        val response = sessionRouter.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        assertEquals(expected = Status.NOT_FOUND, actual = response.status)
        assertEquals(expected = "Player not found", actual = content.message)
    }

    @Test
    fun `addPlayerToSession return response with a session Session not found`() {
        // Arrange
        val request = Request(Method.PUT, Uris.Sessions.ADD_PLAYER.replace("{sid}", "99").replace("{pid}", "1"))
        // Act
        val response = sessionRouter.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        assertEquals(expected = Status.NOT_FOUND, actual = response.status)
        assertEquals(expected = "Session not found", actual = content.message)
    }

    @Test
    fun `addPlayerToSession return response with a session Player already in session`() {
        // Arrange
        repeat(5) {
            createPlayer("test$it@gmail")
        }
        createSession()

        val request = Request(Method.PUT, Uris.Sessions.ADD_PLAYER.replace("{sid}", "1").replace("{pid}", "1"))
        sessionRouter.routes(request)
        // Act
        val response = sessionRouter.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        assertEquals(expected = Status.BAD_REQUEST, actual = response.status)
        assertEquals(expected = "Player already in session", actual = content.message)
    }

    @Test
    fun `addPlayerToSession return response with a session Session full`() {
        // Arrange
        createSession()
        repeat(5) { i ->
            createPlayer("player2$i@gmail.com")
            val request =
                Request(Method.PUT, Uris.Sessions.ADD_PLAYER.replace("{sid}", "10").replace("{pid}", "${i + 1}"))
            val response = sessionRouter.routes(request)
            println(response)
            assertEquals(expected = Status.OK, actual = response.status)
        }
        createPlayer()
        // Act
        val request = Request(Method.PUT, Uris.Sessions.ADD_PLAYER.replace("{sid}", "10").replace("{pid}", "6"))
        val response = sessionRouter.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        assertEquals(expected = Status.BAD_REQUEST, actual = response.status)
        assertEquals(expected = "Session is full", actual = content.message)
    }

    @Test
    fun `getSessions return response with a session Limit and skip`() {
        // Arrange
        createGame()
        session.forEach {
            val r = Request(Method.POST, Uris.DEFAULT).body(
                Json.encodeToString(
                    SessionCreateDTO(it.gid, it.date, it.capacity)
                )
            )
            sessionRouter.routes(r)
        }
        val request = Request(Method.GET, Uris.DEFAULT).query("gid", "1").query("limit", "3").query("skip", "2")
        // Act
        val response = sessionRouter.routes(request)
        // Assert
        val content = Json.decodeFromString<List<SessionDTO>>(response.bodyString())
        assertTrue { response.status.successful }
        assertTrue { response.header(CONTENT_TYPE) == APPLICATION_JSON }
        assertTrue { content.size == 3 }
        assertEquals(session[2].gid, content[0].gid)
        assertEquals(3u, content[0].sid)
        assertEquals(session[3].gid, content[1].gid)
        assertEquals(4u, content[1].sid)
        assertEquals(session[4].gid, content[2].gid)
        assertEquals(5u, content[2].sid)
    }


    companion object {
        private val clock: Clock = Clock.System
        private val sessionDB = SessionMemoryDB(clock)
        private val playerDB = PlayerMemoryDB()
        private val gameDB = GameMemoryDB()
        private val sessionService = SessionService(sessionDB, playerDB, gameDB, clock)
        private val sessionRouter = SessionRouter(sessionService)

        private val session =
            listOf(
                SessionCreateDTO(1u, "2025-03-14T10:58:00.123456789", 10),
                SessionCreateDTO(1u, "2025-03-14T10:58:00.123456789", 10),
                SessionCreateDTO(1u, "2025-03-14T10:58:00.123456789", 10),
                SessionCreateDTO(1u, "2025-03-14T10:58:00.123456789", 10),
                SessionCreateDTO(1u, "2025-03-14T10:58:00.123456789", 10),
            )

    }
}
