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
import pt.isel.ls.sessions.http.model.utils.MessageResponse
import pt.isel.ls.sessions.http.routes.game.GameRouter
import pt.isel.ls.sessions.http.util.APPLICATION_JSON
import pt.isel.ls.sessions.http.util.CONTENT_TYPE
import pt.isel.ls.sessions.http.util.ProblemDTO
import pt.isel.ls.sessions.http.util.Uris
import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.sessions.services.game.GameService
import kotlin.test.*

class GameTests {
    @AfterTest
    fun clear() {
        mem.gameDB.reset()
    }

    private fun createGame() {
        games.forEach {
            router.routes(
                Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(it)).header("Authorization", "Bearer token")
            )
        }
    }

    @Test
    fun `game created with success`() {
        //  Arrange
        val game = GameDTO("cod", "developer", listOf("Action", "Shooter"))
        val request =
            Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(game)).header("Authorization", "Bearer token")

        // Act
        val response = router.routes(request)
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        // Assert
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
        val request =
            Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(game)).header("Authorization", "Bearer token")
        // Act
        router.routes(request)
        val response = router.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(content.status, Status.CONFLICT.code)
        assertTrue(content.title.isNotBlank())
        assertTrue(content.title.contains("Game name already exists"))
        assertTrue(content.type == "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/game-name-already-exists")
    }

    @Test
    fun `game creation fails because no genre is passed`() {
        // Arrange
        val request = Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(gameNoGenres))
            .header("Authorization", "Bearer token")
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
            .header("Authorization", "Bearer token")
        // Act
        val response = router.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(content.status, Status.NOT_FOUND.code)
        assertTrue(content.title == "Genre not found")
        assertTrue(content.detail == "Genre or genres not found")
        assertTrue(content.type == "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/genre-not-found")
    }


    @Test
    fun `get game by id with success`() {
        // Arrange
        val request1 =
            Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(game)).header("Authorization", "Bearer token")
        val request2 = Request(Method.GET, "/1")
        // Act
        router.routes(request1)
        val response = router.routes(request2)
        val content = Json.decodeFromString<GameDTO>(response.bodyString())
        // Assert
        assertTrue { response.header(CONTENT_TYPE).equals(APPLICATION_JSON) }
        assertTrue { response.status.successful }
        assertTrue { content.name == game.name }
        assertTrue { content.developer == game.developer }
        assertTrue { content.genres == game.genres }
    }

    @Test
    fun `get game by id fails because id is invalid`() {
        // Arrange
        val request1 =
            Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(game)).header("Authorization", "Bearer token")
        val request2 = Request(Method.GET, "/2")
        // Act
        router.routes(request1)
        val response = router.routes(request2)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertTrue { response.status == Status.NOT_FOUND }
        assertTrue(content.title == "Game not found")
        assertTrue(content.detail == "Game with given id not found")
        assertEquals(content.status, Status.NOT_FOUND.code)
        assertTrue(content.type == "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/game-not-found")
    }

    @Test
    fun `get all games with success`() {
        // Arrange
        createGame()
        val request =
            Request(Method.GET, Uris.DEFAULT).body(
                Json.encodeToString(
                    GamesInputModel("developer", listOf("Action")),
                ),
            ).header("Authorization", "Bearer token")
        // Act
        val response = router.routes(request)
        val content = Json.decodeFromString<List<GameDTO>>(response.bodyString())
        // Assert
        assertTrue { response.status.successful }
        assertTrue { response.header(CONTENT_TYPE) == APPLICATION_JSON }
        assertTrue { content.size == 3 }
        assertFalse { content.contains(games[1]) }
    }

    @Test
    fun `get all games success despite no games being returned`() {
        // Arrange
        createGame()
        val request =
            Request(Method.GET, Uris.DEFAULT).body(Json.encodeToString(GamesInputModel("developer", listOf("Rpg")))).header("Authorization", "Bearer token")
        // Act
        val response = router.routes(request)
        val content = Json.decodeFromString<List<GameDTO>>(response.bodyString())
        // Assert
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
        createGame()
        val request =
            Request(
                Method.GET,
                Uris.DEFAULT,
            ).body(Json.encodeToString(GamesInputModel("developer3", listOf("Action", "Shooter"))))
        // Act
        val response = router.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertTrue { response.status == Status.NOT_FOUND }
        assertTrue(content.type=="https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/developer-not-found")
        assertTrue { content.title == "Developer not found" }
        assertTrue { content.detail == "Developer not found" }

    }

    @Test
    fun `get all games fails because genres doesn't exist`() {
        // Arrange
        createGame()
        val request =
            Request(Method.GET, Uris.DEFAULT).body(
                Json.encodeToString(
                    GamesInputModel("developer", listOf("Friendly", "Co-op")),
                ),
            )
        // Act
        val response = router.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertTrue { response.status == Status.NOT_FOUND }
        assertTrue(content.type=="https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/genre-not-found")
        assertTrue { content.title == "Genre not found" }
        assertTrue { content.detail == "Genre or genres not found" }
    }

    @Test
    fun `get all games fails because developer not found`() {
        // Arrange
        createGame()
        val request =
            Request(Method.GET, Uris.DEFAULT).body(
                Json.encodeToString(
                    GamesInputModel("d", listOf("sports", "multiplayer")),
                ),
            )
        // Act
        val response = router.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.NOT_FOUND, response.status)
        assertTrue(content.title == "Developer not found")
        assertTrue(content.detail == "Developer not found")
        assertTrue(content.type == "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/developer-not-found")


    }

    @Test
    fun `get all game success with skip(DEFAULT) and limit`() {
        // Arrange
        createGame()
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
        createGame()
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
        createGame()
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
        println(content)
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
        createGame()
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
        assertTrue { content.message == "Invalid limit or skip" }
    }

    @Test
    fun `get all game fails with limit negative`() {
        // Arrange
        createGame()
        val request =
            Request(Method.GET, Uris.DEFAULT).query("limit", "-1").body(
                Json.encodeToString(
                    GamesInputModel("developer", listOf("Action")),
                ),
            )
        // Act
        val response = router.routes(request)
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        // Assert
        assertTrue { response.status == Status.BAD_REQUEST }
        assertTrue { response.header(CONTENT_TYPE) == APPLICATION_JSON }
        assertTrue { content.message == "Invalid limit or skip" }
    }

    companion object {
        private val clock = Clock.System
        private val mem = AppMemoryDB(clock)
        private val service = GameService(mem.gameDB)
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
