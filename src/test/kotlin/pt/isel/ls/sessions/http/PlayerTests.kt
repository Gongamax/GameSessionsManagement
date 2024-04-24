package pt.isel.ls.sessions.http

import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.Test
import pt.isel.ls.sessions.http.model.player.PlayerCreateDTO
import pt.isel.ls.sessions.http.model.player.PlayerDTO
import pt.isel.ls.sessions.http.model.utils.TokenDTO
import pt.isel.ls.sessions.http.routes.player.PlayerRouter
import pt.isel.ls.sessions.http.util.ProblemDTO
import pt.isel.ls.sessions.http.util.Uris
import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.sessions.services.player.PlayerService
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class PlayerTests {
    @AfterTest
    fun clear() {
        mem.playerDB.reset()
    }

    @BeforeTest
    fun createPlayer() {
        val createPlayer = Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(player))
        playerRouter.routes(createPlayer)
    }

    @Test
    fun `create return response with a player`() {
        // Arrange
        val request = Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(player2))
        // Act
        val response = playerRouter.routes(request)
        val pid = response.header("Location")?.split("/")?.last()
        val content = Json.decodeFromString<TokenDTO>(response.bodyString())
        // Assert
        assertEquals("application/json", response.header("Content-Type"))
        assertEquals("/player/$pid", response.header("Location"))
        assertEquals(Status.CREATED, response.status)
        assertEquals(TokenDTO(content.pid, content.token), content)
    }

    @Test
    fun `create return response with a player already exists`() {
        // Arrange
        val name = "Bob"
        val request = Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(PlayerCreateDTO(name, EMAIL)))
        // Act
        val response = playerRouter.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.BAD_REQUEST, response.status)
        assertEquals("application/problem+json", response.header("Content-Type"))
        assertEquals("Player with given email Alice@gmail.com already exists", content.detail)
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/email-already-exists",
            content.type,
        )
        assertEquals("Email already exists", content.title)
    }

    @Test
    fun `create return response with a player invalid email`() {
        // Arrange
        val request = Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(playerInvalidEmail))
        // Act
        val response = playerRouter.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.BAD_REQUEST, response.status)
        assertEquals("application/problem+json", response.header("Content-Type"))
        assertEquals("Email is invalid", content.detail)
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/invalid-email",
            content.type,
        )
        assertEquals("Invalid email", content.title)
    }

    @Test
    fun `create return response with a player name already exists`() {
        // Arrange
        val request = Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(playerNameAlreadyExists))
        // Act
        val response = playerRouter.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.BAD_REQUEST, response.status)
        assertEquals("application/problem+json", response.header("Content-Type"))
        assertEquals("Given name Alice already exists", content.detail)
    }

    @Test
    fun `getDetailsPlayer return response with a player`() {
        // Arrange
        val request =
            Request(Method.GET, Uris.Players.BY_ID.replace("{pid}", "1")).header("Authorization", "Bearer token")
        // Act
        val response = playerRouter.routes(request)
        val content = Json.decodeFromString<PlayerDTO>(response.bodyString())
        // Assert
        assertEquals(Status.OK, response.status)
        assertEquals(NAME, content.name)
        assertEquals(EMAIL, content.email)
    }

    @Test
    fun `getDetailsPlayer return response with a player not found`() {
        // Arrange
        val request =
            Request(Method.GET, Uris.Players.BY_ID.replace("{pid}", "99")).header("Authorization", "Bearer token")
        // Act
        val response = playerRouter.routes(request)
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
    fun `getDetailsPlayer return response with a player invalid token`() {
        // Arrange
        val request =
            Request(Method.GET, Uris.Players.BY_ID.replace("{pid}", "1"))
        // Act
        val response = playerRouter.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.UNAUTHORIZED, response.status)
        assertEquals("application/problem+json", response.header("Content-Type"))
        assertEquals("Unauthorized, token not found", content.detail)
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/token-not-found",
            content.type,
        )
        assertEquals("Token not found", content.title)
    }

    companion object {
        private val clock = Clock.System
        private val mem = AppMemoryDB(clock)
        private val playerService = PlayerService(mem.playerDB)
        private val playerRouter = PlayerRouter(playerService)
        const val NAME = "Alice"
        const val EMAIL = "Alice@gmail.com"
        private val player = PlayerCreateDTO(NAME, EMAIL)
        private val player2 = PlayerCreateDTO("Bob", "Bob@gmail.com")
        private val playerInvalidEmail = PlayerCreateDTO(NAME, "Alice@gmail")
        private val playerNameAlreadyExists = PlayerCreateDTO(NAME, "xpto@gmail.com")
    }
}
