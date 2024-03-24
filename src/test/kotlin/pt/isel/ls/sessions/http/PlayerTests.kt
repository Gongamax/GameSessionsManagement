package pt.isel.ls.sessions.http

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.junit.Test
import pt.isel.ls.sessions.http.model.player.PlayerDTO
import pt.isel.ls.sessions.http.model.utils.MessageResponse
import pt.isel.ls.sessions.http.routes.player.PlayerRouter
import pt.isel.ls.sessions.http.util.ProblemDTO
import pt.isel.ls.sessions.http.util.Uris
import pt.isel.ls.sessions.repository.data.player.PlayerMemoryDB
import pt.isel.ls.sessions.services.player.PlayerService
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PlayerTests {
    companion object {
        const val NAME = "Alice"
        const val EMAIL = "Alice@gmail.com"
        private val playerDB = PlayerMemoryDB()
        private val playerService = PlayerService(playerDB)
        private val playerRouter = PlayerRouter(playerService)
    }

    @BeforeTest
    fun createPlayer() {
        val createPlayer = Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(PlayerDTO(NAME, EMAIL)))
        playerRouter.routes(createPlayer)
    }

    @Test
    fun `create return response with a player`() {
        // Arrange
        val name = "Francisco"
        val email = "francisco@gmail.com"
        val request = Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(PlayerDTO(name, email)))
        // Act
        val response = playerRouter.routes(request)
        // Assert
        assertTrue { response.header("Location") != null }
        val pid = response.header("Location")?.split("/")?.last()
        assertEquals(expected = "/player/$pid", actual = response.header("Location"))
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        assertEquals(expected = Status.CREATED, actual = response.status)
        assertEquals(expected = "Player create: $pid", actual = content.message)
    }

    @Test
    fun `create return response with a player already exists`() {
        // Arrange
        val name = "Bob"
        val request = Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(PlayerDTO(name, EMAIL)))
        // Act
        val response = playerRouter.routes(request)
        // Assert
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        assertEquals(expected = Status.CONFLICT, actual = response.status)
        assertEquals(expected = "Player with given email $EMAIL already exists", actual = content.detail)
    }

    @Test
    fun `getDetailsPlayer return response with a player`() {
        // Arrange
        createPlayer()
        val request = Request(Method.GET, Uris.Players.BY_ID.replace("{pid}", "1"))
        // Act
        val response = playerRouter.routes(request)
        // Assert
        val content = Json.decodeFromString<PlayerDTO>(response.bodyString())
        assertEquals(expected = Status.OK, actual = response.status)
        assertEquals(expected = NAME, actual = content.name)
        assertEquals(expected = EMAIL, actual = content.email)
    }

    @Test
    fun `getDetailsPlayer return response with a player not found`() {
        // Arrange
        val request = Request(Method.GET, Uris.Players.BY_ID.replace("{pid}", "99"))
        // Act
        val response = playerRouter.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        assertEquals(expected = Status.NOT_FOUND, actual = response.status)
        assertEquals(expected = "Player not found", actual = content.message)
    }
}
