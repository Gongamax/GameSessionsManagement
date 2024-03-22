package pt.isel.ls.sessions.http

import junit.framework.TestCase
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import pt.isel.ls.sessions.http.model.game.GameDTO
import pt.isel.ls.sessions.http.model.game.GamesInputModel
import pt.isel.ls.sessions.http.model.session.SessionDTO
import pt.isel.ls.sessions.http.model.utils.MessageResponse
import pt.isel.ls.sessions.http.routes.game.GameRouter
import pt.isel.ls.sessions.http.util.APPLICATION_JSON
import pt.isel.ls.sessions.http.util.CONTENT_TYPE
import pt.isel.ls.sessions.http.util.Uris
import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.sessions.services.game.GameService
import kotlin.test.*

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
        assertEquals(Status.CREATED, response.status)
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
        assertTrue { content.message == "Game name already exists" }
        assertEquals(Status.CONFLICT, response.status)
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
        assertEquals(Status.EXPECTATION_FAILED, response.status)
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

        assertEquals(Status.BAD_REQUEST, response.status)
        assertEquals(response.header(CONTENT_TYPE), APPLICATION_JSON)
        assertTrue { response.status.clientError }
        assertEquals(Status.BAD_REQUEST, response.status)
        assertTrue { content.message.isNotBlank() }
        assertTrue { content.message == "Invalid genre, please check the genre name" }
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
        assertTrue { response.header(CONTENT_TYPE) == APPLICATION_JSON }
        assertTrue { response.bodyString().contains("Game not found") }
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
        assertTrue { content.size == 3 }
        assertFalse { content.contains(games[1]) }
    }

    @Test
    fun `get all games success despite no games being returned`() {
        // Arrange
        games.forEach { router.routes(Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(it))) }
        val request =
            Request(Method.GET, Uris.DEFAULT).body(Json.encodeToString(GamesInputModel("developer", listOf("Rpg"))))
        // Act
        val response = router.routes(request)
        // Assert
        val content = Json.decodeFromString<List<GameDTO>>(response.bodyString())

        assertTrue { response.status == Status.OK }
        assertTrue { response.header(CONTENT_TYPE) == APPLICATION_JSON }
        assertEquals(expected = Status.OK, actual = response.status)
        assertTrue(content.isEmpty())
        assertTrue(content.isEmpty())
        TestCase.assertEquals("[]", content.toString())
    }

    @Test
    fun `get all games fails because developer doesn't exist`() {
        // Arrange
        games.forEach { router.routes(Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(it))) }
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

    @Test
    fun `get all games fails because developer not found`() {
        // Arrange
        games.forEach { router.routes(Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(it))) }
        val request =
            Request(Method.GET, Uris.DEFAULT).body(
                Json.encodeToString(
                    GamesInputModel("d", listOf("sports", "multiplayer")),
                ),
            )
        // Act
        val response = router.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        assertEquals(Status.NOT_FOUND, response.status)
        assertEquals("Developer not found", content.message)
    }

    @Test
    fun `get all game success with skip(DEFAULT) and limit`() {
        // Arrange
        games.forEach { router.routes(Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(it))) }
        val request =
            Request(Method.GET, Uris.DEFAULT).query("limit", "2").body(
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
        assertTrue { content.contains(games[0]) }
        assertFalse { content.contains(games[1]) }
        assertTrue { content.contains(games[2]) }
        assertFalse(content.contains(games[3]))
    }

    @Test
    fun `get all game success with skip and limit(DEFAULT)`() {
        // Arrange
        games.forEach { router.routes(Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(it))) }
        val request =
            Request(Method.GET, Uris.DEFAULT).query("skip", "1").body(
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
        assertFalse { content.contains(games[0]) }
        assertFalse { content.contains(games[1]) }
        assertTrue { content.contains(games[2]) }
        assertTrue() { content.contains(games[3]) }
    }

    @Test
    fun `get all game success with skip and limit`() {
        // Arrange
        games.forEach { router.routes(Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(it))) }
        val request =
            Request(Method.GET, Uris.DEFAULT).query("limit", "1").query("skip", "1").body(
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
        assertTrue { content.size == 1 }
        assertFalse { content.contains(games[0]) }
        assertFalse { content.contains(games[1]) }
        assertTrue { content.contains(games[2]) }
        assertFalse { content.contains(games[3]) }
    }

    @Test
    fun `get all game fails with skip negative`() {
        // Arrange
        games.forEach { router.routes(Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(it))) }
        val request =
            Request(Method.GET, Uris.DEFAULT).query("skip", "-1").body(
                Json.encodeToString(
                    GamesInputModel("developer", listOf("Action")),
                ),
            )
        // Act
        val response = router.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())

        assertTrue { response.status == Status.BAD_REQUEST }
        assertTrue { response.header(CONTENT_TYPE) == APPLICATION_JSON }
        assertTrue { content.message == "Limit or skip is negative" }
    }

    @Test
    fun `get all game fails with limit negative`() {
        // Arrange
        games.forEach { router.routes(Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(it))) }
        val request =
            Request(Method.GET, Uris.DEFAULT).query("limit", "-1").body(
                Json.encodeToString(
                    GamesInputModel("developer", listOf("Action")),
                ),
            )
        // Act
        val response = router.routes(request)
        // Assert
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())

        assertTrue { response.status == Status.BAD_REQUEST }
        assertTrue { response.header(CONTENT_TYPE) == APPLICATION_JSON }
        assertTrue { content.message == "Limit or skip is negative" }
    }

    companion object {
        private val clock = Clock.System
        private val mem = AppMemoryDB(clock)
        private val service = GameService(mem)
        private val router = GameRouter(service)
        private val game = GameDTO("cod", "developer", listOf("Action", "Shooter"))
        private val gameInvalidGenre = GameDTO("cod", "developer", listOf("multiplayer", "sport", "Action"))
        private val gameNoGenres = GameDTO("cod", "developer", listOf())
        private val games =
            listOf(
                GameDTO("cod", "developer", listOf("Action", "Shooter")),
                GameDTO("cs-go", "developer1", listOf("Action", "Shooter")),
                GameDTO("over-watch", "developer", listOf("Action", "Shooter")),
                GameDTO("FIFA", "developer", listOf("Action", "Sports")),
            )
    }
}
