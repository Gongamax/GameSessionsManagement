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
import pt.isel.ls.sessions.http.model.player.PlayerCreateDTO
import pt.isel.ls.sessions.http.model.session.SessionCreateDTO
import pt.isel.ls.sessions.http.model.session.SessionDTO
import pt.isel.ls.sessions.http.model.session.SessionUpdateDTO
import pt.isel.ls.sessions.http.model.session.SessionsDTO
import pt.isel.ls.sessions.http.model.utils.MessageResponse
import pt.isel.ls.sessions.http.routes.player.PlayerRouter
import pt.isel.ls.sessions.http.routes.session.SessionRouter
import pt.isel.ls.sessions.http.util.ProblemDTO
import pt.isel.ls.sessions.http.util.Uris
import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.sessions.repository.data.game.GameMemoryDB
import pt.isel.ls.sessions.repository.data.player.PlayerMemoryDB
import pt.isel.ls.sessions.services.player.PlayerService
import pt.isel.ls.sessions.services.session.SessionService
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
        mem.gameDB.reset()
        mem.playerDB.reset()
        mem.sessionDB.reset()
    }

    private fun createSession(createOne: Boolean = false): Any? =
        if (createOne) {
            sessionRouter.routes(
                Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(sessions[0]))
                    .header("Authorization", "Bearer token"),
            ).header("Location")?.split("/")?.last()
        } else {
            sessions.forEach {
                sessionRouter.routes(
                    Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(it))
                        .header("Authorization", "Bearer token"),
                )
            }
        }

    private fun createPlayer(
        name: String = "Francisco",
        email: String = "francisco@gmail.com",
    ) {
        val createPlayer =
            Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(PlayerCreateDTO(name, email)))
                .header("Authorization", "Bearer token")
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
                .header("Authorization", "Bearer token")
        // Act
        val response = sessionRouter.routes(request)
        val sid = response.header("Location")?.split("/")?.last()
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        // Assert
        assertEquals("/sessions/$sid", response.header("Location"))
        assertEquals(Status.CREATED, response.status)
        assertEquals("Session created: $sid", content.message)
    }

    @Test
    fun `create return response with a session Game not found`() {
        // Arrange
        val capacity = 10
        val gid = 99u
        val date = "2025-03-14T10:58:00.123456789"
        val request =
            Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(SessionCreateDTO(gid, date, capacity)))
                .header("Authorization", "Bearer token")
        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.NOT_FOUND, response.status)
        assertEquals("application/problem+json", response.header("Content-Type"))
        assertEquals("Game with given id not found", content.detail)
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/game-not-found",
            content.type,
        )
        assertEquals("Game not found", content.title)
    }

    @Test
    fun `create return response with a session Invalid date`() {
        // Arrange
        val capacity = 10
        val gid = 1u
        val date = "2020-03-14T10:58:00.123456789"
        val request =
            Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(SessionCreateDTO(gid, date, capacity)))
                .header("Authorization", "Bearer token")
        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.BAD_REQUEST, response.status)
        assertEquals("application/problem+json", response.header("Content-Type"))
        assertEquals("Date is invalid", content.detail)
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/invalid-date",
            content.type,
        )
        assertEquals("Invalid date", content.title)
    }

    @Test
    fun `create return response with a session Invalid capacity`() {
        // Arrange
        val capacity = 0
        val gid = 1u
        val date = "2025-03-14T10:58:00.123456789"
        val request =
            Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(SessionCreateDTO(gid, date, capacity)))
                .header("Authorization", "Bearer token")
        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.BAD_REQUEST, response.status)
        assertEquals("application/problem+json", response.header("Content-Type"))
        assertEquals("Capacity is invalid", content.detail)
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/invalid-capacity",
            content.type,
        )
        assertEquals("Invalid capacity", content.title)
    }

    @Test
    fun `getSession return response with a session`() {
        // Arrange
        createSession()
        val gid = 1u
        val request =
            Request(Method.GET, Uris.Sessions.BY_ID.replace("{sid}", "1")).header("Authorization", "Bearer token")

        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<SessionDTO>(response.bodyString())
        // Assert
        assertEquals(Status.OK, response.status)
        assertEquals("application/json", response.header("Content-Type"))
        assertEquals(1u, content.sid)
        assertEquals(gid, content.gid)
        assertEquals(10, content.capacity)
        assertEquals("2025-03-14T10:58:00.123456789", content.date.toString())
    }

    @Test
    fun `getSession return response with a session not found`() {
        // Arrange
        val request =
            Request(Method.GET, Uris.Sessions.BY_ID.replace("{sid}", "99")).header("Authorization", "Bearer token")
        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.NOT_FOUND, response.status)
        assertEquals("application/problem+json", response.header("Content-Type"))
        assertEquals("Session with given id: 99 not found", content.detail)
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/session-not-found",
            content.type,
        )
        assertEquals("Session not found", content.title)
    }

    @Test
    fun `getSessions return response with a session`() {
        // Arrange
        createSession()
        val request = Request(Method.GET, Uris.DEFAULT).query("gid", "1")
        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<SessionsDTO>(response.bodyString())
        // Assert
        assertEquals(Status.OK, response.status)
        assertEquals("application/json", response.header("Content-Type"))
        assertEquals(1u, content.sessions[0].sid)
        assertEquals(1u, content.sessions[0].gid)
        assertEquals(10, content.sessions[0].capacity)
        assertEquals("2025-03-14T10:58:00.123456789", content.sessions[0].date.toString())
    }

    @Test
    fun `getSessions return response with a session State OPEN`() {
        // Arrange
        createSession()
        val request = Request(Method.GET, Uris.DEFAULT).query("gid", "1").query("state", "OPEN")

        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<SessionsDTO>(response.bodyString())
        // Assert
        assertEquals(Status.OK, response.status)
        assertEquals("application/json", response.header("Content-Type"))
        assertEquals(1u, content.sessions[0].sid)
        assertEquals(1u, content.sessions[0].gid)
        assertEquals(10, content.sessions[0].capacity)
        assertEquals("2025-03-14T10:58:00.123456789", content.sessions[0].date.toString())
    }

    @Test
    fun `getSessions return response with a session Game not found`() {
        // Arrange
        createGame()
        val request = Request(Method.GET, Uris.DEFAULT).query("gid", "2")
        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<SessionsDTO>(response.bodyString())
        // Assert
        assertEquals(Status.OK, response.status)
        assertEquals("application/json", response.header("Content-Type"))
        assertTrue(content.sessions.isEmpty())
        assertEquals("[]", content.sessions.toString())
    }

    @Test
    fun `getSessions return response with a session Invalid date`() {
        // Arrange
        createSession()
        val request = Request(Method.GET, Uris.DEFAULT).query("gid", "1").query("date", "2020-14-03T10:58:00.123456789")
        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.BAD_REQUEST, response.status)
        assertEquals("application/problem+json", response.header("Content-Type"))
        assertEquals("Date is invalid", content.detail)
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/invalid-date",
            content.type,
        )
        assertEquals("Invalid date", content.title)
    }

    @Test
    fun `getSessions return response with a session Invalid state`() {
        // Arrange
        createSession()
        val request = Request(Method.GET, Uris.DEFAULT).query("gid", "1").query("state", "OPES")
        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.BAD_REQUEST, response.status)
        assertEquals("application/problem+json", response.header("Content-Type"))
        assertEquals("State is invalid", content.detail)
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/invalid-state",
            content.type,
        )
        assertEquals("Invalid state", content.title)
    }

    @Test
    fun `getSessions return response with a session Player not found`() {
        // Arrange
        createSession()
        val request = Request(Method.GET, Uris.DEFAULT).query("gid", "1").query("pid", "99")
        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.NOT_FOUND, response.status)
        assertEquals("application/problem+json", response.header("Content-Type"))
        assertEquals("Player with given id: 99 not found", content.detail)
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/player-not-found",
            content.type,
        )
        assertEquals("Player not found", content.title)
    }

    @Test
    fun `getSessions return response with a session Limit or skip is negative`() {
        // Arrange
        createSession()
        val request =
            Request(Method.GET, Uris.DEFAULT).query("gid", "1").query("limit", "-1").query("skip", "-1")
                .header("Authorization", "Bearer token")
        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.BAD_REQUEST, response.status)
        assertEquals("application/problem+json", response.header("Content-Type"))
        assertEquals("Invalid skip or limit", content.detail)
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/invalid-skip-or-limit",
            content.type,
        )
        assertEquals("Invalid skip or limit", content.title)
    }

    @Test
    fun `addPlayerToSession return response with a session`() {
        // Arrange
        createSession()
        createPlayer()
        val request =
            Request(
                Method.PUT,
                Uris.Sessions.ADD_PLAYER.replace("{sid}", "1").replace("{pid}", "1"),
            ).header("Authorization", "Bearer token")
        // Act<
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        // Assert
        assertEquals(expected = Status.NO_CONTENT, actual = response.status)
        assertEquals(expected = "Player added to session", actual = content.message)
    }

    @Test
    fun `addPlayerToSession return response with a session Player not found`() {
        // Arrange
        createSession()
        val request =
            Request(
                Method.PUT,
                Uris.Sessions.ADD_PLAYER.replace("{sid}", "1").replace("{pid}", "999"),
            ).header("Authorization", "Bearer token")
        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.NOT_FOUND, response.status)
        assertEquals("Player with given id: 999 not found", content.detail)
        assertEquals("application/problem+json", response.header("Content-Type"))
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/player-not-found",
            content.type,
        )
        assertEquals("Player not found", content.title)
    }

    @Test
    fun `addPlayerToSession return response with a session Session not found`() {
        // Arrange
        val request =
            Request(
                Method.PUT,
                Uris.Sessions.ADD_PLAYER.replace("{sid}", "99").replace("{pid}", "1"),
            ).header("Authorization", "Bearer token")
        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.NOT_FOUND, response.status)
        assertEquals("application/problem+json", response.header("Content-Type"))
        assertEquals("Session with given id: 99 not found", content.detail)
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/session-not-found",
            content.type,
        )
        assertEquals("Session not found", content.title)
    }

    @Test
    fun `addPlayerToSession return response with a session Player already in session`() {
        // Arrange
        repeat(5) { createPlayer("test$it@gmail.com") }
        createSession()
        val request =
            Request(
                Method.PUT,
                Uris.Sessions.ADD_PLAYER.replace("{sid}", "1").replace("{pid}", "1"),
            ).header("Authorization", "Bearer token")
        sessionRouter.routes(request)
        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.BAD_REQUEST, response.status)
        assertEquals("Player is already in session", content.detail)
        assertEquals("application/problem+json", response.header("Content-Type"))
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/player-already-in-session",
            content.type,
        )
        assertEquals("Player already in session", content.title)
    }

    @Test
    fun `addPlayerToSession return response with a session Session full`() {
        // Arrange
        val sid = 5
        val pid = 6
        createSession()
        repeat(5) { i ->
            createPlayer("player$i", "player2$i@gmail.com")
            val request =
                Request(
                    Method.PUT,
                    Uris.Sessions.ADD_PLAYER.replace("{sid}", "$sid").replace("{pid}", "${i + 1}"),
                ).header("Authorization", "Bearer token")
            val response = sessionRouter.routes(request)
            assertEquals(expected = Status.NO_CONTENT, actual = response.status)
        }
        createPlayer()
        // Act
        val request =
            Request(
                Method.PUT,
                Uris.Sessions.ADD_PLAYER.replace("{sid}", "$sid").replace("{pid}", "$pid"),
            ).header("Authorization", "Bearer token")
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.BAD_REQUEST, response.status)
        assertEquals("application/problem+json", response.header("Content-Type"))
        assertEquals("Session is full", content.detail)
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/session-is-full",
            content.type,
        )
        assertEquals("Session is full", content.title)
        assertEquals("/$sid/player/$pid", content.instance)
    }

    @Test
    fun `getSessions return response with a session Limit and skip`() {
        // Arrange
        createGame()
        createSession()
        val request = Request(Method.GET, Uris.DEFAULT).query("gid", "1").query("limit", "3").query("skip", "2")
        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<SessionsDTO>(response.bodyString())
        // Assert
        assertEquals(Status.OK, response.status)
        assertEquals("application/json", response.header("Content-Type"))
        assertEquals(3, content.sessions.size)
        assertEquals(sessions[2].gid, content.sessions[0].gid)
        assertEquals(sessions[3].gid, content.sessions[1].gid)
        assertEquals(sessions[4].gid, content.sessions[2].gid)
        assertEquals(sessions[2].date, content.sessions[0].date.toString())
        assertEquals(sessions[3].date, content.sessions[1].date.toString())
        assertEquals(sessions[4].date, content.sessions[2].date.toString())
        assertEquals(sessions[2].capacity, content.sessions[0].capacity)
        assertEquals(sessions[3].capacity, content.sessions[1].capacity)
        assertEquals(sessions[4].capacity, content.sessions[2].capacity)
    }

    @Test
    fun `deleteSession succeeds`() {
        // Arrange
        val sid = createSession(true) as String
        val request =
            Request(Method.DELETE, Uris.Sessions.BY_ID.replace("{sid}", sid))
                .header("Authorization", "Bearer token")
        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        // Assert
        assertEquals(Status.NO_CONTENT, response.status)
        assertEquals("Session deleted", content.message)
    }

    @Test
    fun `deleteSession returns session not found`() {
        // Arrange
        val request =
            Request(Method.DELETE, Uris.Sessions.BY_ID.replace("{sid}", "99"))
                .header("Authorization", "Bearer token")
        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.NOT_FOUND, response.status)
        assertEquals("Session with given id: 99 not found", content.detail)
        assertEquals("application/problem+json", response.header("Content-Type"))
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/session-not-found",
            content.type,
        )
        assertEquals("Session not found", content.title)
    }

    @Test
    fun `updateSession return response with a session`() {
        // Arrange
        createSession()
        val capacity = 5
        val date = "2025-03-14T10:58:00.123456789".toLocalDateTime()
        val sid = 1
        val request =
            Request(Method.PUT, Uris.Sessions.BY_ID.replace("{sid}", "$sid"))
                .body(Json.encodeToString(SessionUpdateDTO(capacity, date)))
                .header("Authorization", "Bearer token")
        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        // Assert
        assertEquals(Status.NO_CONTENT, response.status)
        assertEquals("Session updated $sid", content.message)
    }

    @Test
    fun `updateSession return response with a session Session not found`() {
        // Arrange
        val capacity = 5
        val date = "2025-03-14T10:58:00.123456789".toLocalDateTime()
        val sid = 99
        val request =
            Request(Method.PUT, Uris.Sessions.BY_ID.replace("{sid}", "$sid"))
                .body(Json.encodeToString(SessionUpdateDTO(capacity, date)))
                .header("Authorization", "Bearer token")
        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.NOT_FOUND, response.status)
        assertEquals("Session with given id: 99 not found", content.detail)
        assertEquals("application/problem+json", response.header("Content-Type"))
        assertEquals("Session not found", content.title)
    }

    @Test
    fun `updateSession return response with a session Invalid date`() {
        // Arrange
        createSession()
        // Arrange
        val capacity = 5
        val date = "2020-03-14T10:58:00.123456789".toLocalDateTime()
        val sid = 1
        val request =
            Request(Method.PUT, Uris.Sessions.BY_ID.replace("{sid}", "$sid"))
                .body(Json.encodeToString(SessionUpdateDTO(capacity, date)))
                .header("Authorization", "Bearer token")
        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.BAD_REQUEST, response.status)
        assertEquals("Date is invalid", content.detail)
        assertEquals("application/problem+json", response.header("Content-Type"))
        assertEquals("Invalid date", content.title)
    }

    @Test
    fun `updateSession return response with a session Invalid capacity`() {
        // Arrange
        createSession()
        // Arrange
        val capacity = 0
        val date = "2025-03-14T10:58:00.123456789".toLocalDateTime()
        val sid = 1
        val request =
            Request(Method.PUT, Uris.Sessions.BY_ID.replace("{sid}", "$sid"))
                .body(Json.encodeToString(SessionUpdateDTO(capacity, date)))
                .header("Authorization", "Bearer token")
        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.BAD_REQUEST, response.status)
        assertEquals("Capacity is invalid", content.detail)
        assertEquals("application/problem+json", response.header("Content-Type"))
        assertEquals("Invalid capacity", content.title)
    }

    @Test
    fun `updateSession return response with a session Invalid Token`() {
        // Arrange
        createSession()
        val capacity = 5
        val date = "2025-03-14T10:58:00.123456789".toLocalDateTime()
        val sid = 1
        val request =
            Request(Method.PUT, Uris.Sessions.BY_ID.replace("{sid}", "$sid"))
                .body(Json.encodeToString(SessionUpdateDTO(capacity, date)))
        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.UNAUTHORIZED, response.status)
        assertEquals("application/problem+json", response.header("Content-Type"))
        assertEquals("Unauthorized, token not found", content.detail)
        assertEquals("Token not found", content.title)
    }

    @Test
    fun `removePlayerFromSession succeeds`() {
        // Arrange
        createSession()
        createPlayer()
        val request =
            Request(
                Method.DELETE,
                Uris.Sessions.REMOVE_PLAYER.replace("{sid}", "1").replace("{pid}", "1"),
            ).header("Authorization", "Bearer token")
        sessionRouter.routes(
            Request(
                Method.PUT,
                Uris.Sessions.ADD_PLAYER.replace("{sid}", "1").replace("{pid}", "1"),
            ).header("Authorization", "Bearer token"),
        )
        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        // Assert
        assertEquals(Status.NO_CONTENT, response.status)
        assertEquals("Player removed from session", content.message)
    }

    @Test
    fun `removePlayerFromSession returns session not found`() {
        // Arrange
        createPlayer()
        val request =
            Request(
                Method.DELETE,
                Uris.Sessions.REMOVE_PLAYER.replace("{sid}", "99").replace("{pid}", "1"),
            ).header("Authorization", "Bearer token")
        // Act
        val response = sessionRouter.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.NOT_FOUND, response.status)
        assertEquals("Session with given id: 99 not found", content.detail)
        assertEquals("application/problem+json", response.header("Content-Type"))
    }

    companion object {
        private val clock: Clock = Clock.System
        private val mem = AppMemoryDB(clock)
        private val playerDB = PlayerMemoryDB()
        private val gameDB = GameMemoryDB()
        private val sessionService = SessionService(mem.sessionDB, playerDB, gameDB, clock)
        private val sessionRouter = SessionRouter(sessionService)

        private val sessions =
            listOf(
                SessionCreateDTO(1u, "2025-03-14T10:58:00.123456789", 10),
                SessionCreateDTO(1u, "2025-03-14T10:58:00.123456789", 10),
                SessionCreateDTO(1u, "2025-03-14T10:58:00.123456789", 10),
                SessionCreateDTO(1u, "2025-03-14T10:58:00.123456789", 10),
                SessionCreateDTO(1u, "2025-03-14T10:58:00.123456789", 5),
            )
    }
}
