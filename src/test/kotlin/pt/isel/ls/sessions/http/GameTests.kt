package pt.isel.ls.sessions.http

import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import pt.isel.ls.sessions.http.model.game.GameDTO
import pt.isel.ls.sessions.http.model.game.GamesInputModel
import pt.isel.ls.sessions.http.model.utils.MessageResponse
import pt.isel.ls.sessions.http.routes.game.GameRouter
import pt.isel.ls.sessions.http.util.APPLICATION_JSON
import pt.isel.ls.sessions.http.util.CONTENT_TYPE
import pt.isel.ls.sessions.http.util.Uris
import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.sessions.services.game.GameService
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameTests {
    @AfterTest
    fun clear() {
        mem.gameDB.reset()
    }

    @Test
    fun `game created with success`() {
        //  Arrange
        val game = GameDTO("cod", "developer", listOf("Action", "Shooter"))
        val request = Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(game))
        // Act
        val response = router.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())

        assertTrue { response.header("Location").equals("/game/1") }
        assertTrue { response.status.successful }
        assertTrue { content.message.isNotBlank() }
        assertTrue { content.message.contains("Game id:") }
    }

    @Test
    fun `game creation fails because name already exists`() {
        // Arrange
        val game = GameDTO("cod", "developer", listOf("Action", "Shooter"))
        val request = Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(game))
        // Act
        router.routes(request)
        val response = router.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())

        assertTrue { response.header(CONTENT_TYPE).equals(APPLICATION_JSON) }
        assertTrue { response.status.clientError }
        assertTrue { content.message.isNotBlank() }
        assertTrue { content.message == "The game name already exists." }
    }

    @Test
    fun `game creation fails because no genre is passed`() {
        // Arrange
        val request = Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(gameNoGenres))
        // Act
        val response = router.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())

        assertTrue { response.header(CONTENT_TYPE).equals(APPLICATION_JSON) }
        assertTrue { response.status.clientError }
        assertTrue { content.message.isNotBlank() }
        assertTrue { content.message == "Invalid game data." }
    }

    @Test
    fun `game creation fails because genre is invalid`() {
        // Arrange
        val request = Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(gameInvalidGenre))
        // Act
        val response = router.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())

        assertTrue { response.header(CONTENT_TYPE).equals(APPLICATION_JSON) }
        assertTrue { response.status.clientError }
        assertTrue { content.message.isNotBlank() }
        assertTrue { content.message == "The genre is invalid." }
    }

    @Test
    fun `get game by id with success`() {
        // Arrange
        val request1 = Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(game))
        val request2 = Request(Method.GET, "/1")
        // Act
        router.routes(request1)
        val response = router.routes(request2)
        // Assert
        val content = Json.decodeFromString<GameDTO>(response.bodyString())

        assertTrue { response.header(CONTENT_TYPE).equals(APPLICATION_JSON) }
        assertTrue { response.status.successful }
        assertTrue { content.name == game.name }
        assertTrue { content.developer == game.developer }
        assertTrue { content.genres == game.genres }
    }

    @Test
    fun `get game by id fails because id is invalid`() {
        // Arrange
        val request1 = Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(game))
        val request2 = Request(Method.GET, "/2")
        // Act
        router.routes(request1)
        val response = router.routes(request2)
        // Assert
        assertTrue { response.status == Status.NOT_FOUND }
    }

    @Test
    fun `get all games with success`() {
        // Arrange
        games.forEach { router.routes(Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(it))) }
        val request =
            Request(Method.GET, Uris.DEFAULT).body(
                Json.encodeToString(
                    GamesInputModel("developer", listOf("Action")),
                ),
            )
        // Act
        val response = router.routes(request)
        // Assert
        val content = Json.decodeFromString<List<GameDTO>>(response.bodyString())

        assertTrue { response.status.successful }
        assertTrue { response.header(CONTENT_TYPE) == APPLICATION_JSON }
        assertTrue { content.size == 2 }
        assertFalse { content.contains(games[1]) }
    }

    @Test
    fun `get all games fails because no game is returned`() {
        // Arrange
        games.forEach { router.routes(Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(it))) }
        val request = Request(Method.GET, Uris.DEFAULT).body(Json.encodeToString(GamesInputModel("developer", listOf("Rpg"))))
        // Act
        val response = router.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())

        assertTrue { response.status == Status.NOT_FOUND }
        assertTrue { response.header(CONTENT_TYPE) == APPLICATION_JSON }
        assertTrue { content.message == "Games not found" }
    }

    @Test
    fun `get all games fails because developer doesn't exist`() {
        // Arrange
        games.forEach { router.routes(Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(it))).also { println(it) } }
        val request =
            Request(
                Method.GET,
                Uris.DEFAULT,
            ).body(Json.encodeToString(GamesInputModel("developer3", listOf("Action", "Shooter"))))
        // Act
        val response = router.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())

        assertTrue { response.status == Status.NOT_FOUND }
        assertTrue { response.header(CONTENT_TYPE) == APPLICATION_JSON }
        assertTrue { content.message == "Developer not found" }
    }

    @Test
    fun `get all games fails because genres doesn't exist`() {
        // Arrange
        games.forEach { router.routes(Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(it))) }
        val request =
            Request(Method.GET, Uris.DEFAULT).body(
                Json.encodeToString(
                    GamesInputModel("developer", listOf("Friendly", "Co-op")),
                ),
            )
        // Act
        val response = router.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())

        assertTrue { response.status == Status.NOT_FOUND }
        assertTrue { response.header(CONTENT_TYPE) == APPLICATION_JSON }
        assertTrue { content.message == "Genre not found" }
    }

    companion object {
        private val clock = Clock.System
        private val mem = AppMemoryDB(clock)
        private val service = GameService(mem)
        private val router = GameRouter(service)
        private val game = GameDTO("cod", "developer", listOf("Action", "Shooter"))
        private val gameInvalidGenre = GameDTO("cod", "developer", listOf("Friendly"))
        private val gameNoGenres = GameDTO("cod", "developer", listOf())
        private val games =
            listOf(
                GameDTO("cod", "developer", listOf("Action", "Shooter")),
                GameDTO("cs-go", "developer1", listOf("Action", "Shooter")),
                GameDTO("over-watch", "developer", listOf("Action", "Shooter")),
            )
    }
}
