package pt.isel.ls.sessions.http

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.junit.Test
import pt.isel.ls.sessions.http.model.player.PlayerDTO
import pt.isel.ls.sessions.http.model.utils.MessageResponse
import pt.isel.ls.sessions.http.routes.player.PlayerRouter
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
        assertEquals(expected = 201, actual = response.status.code)
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
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        assertEquals(expected = 400, actual = response.status.code)
        assertEquals(expected = "Email already exists", actual = content.message)
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
        assertEquals(expected = 200, actual = response.status.code)
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
        assertEquals(expected = 404, actual = response.status.code)
        assertEquals(expected = "Player not found", actual = content.message)
    }
}
