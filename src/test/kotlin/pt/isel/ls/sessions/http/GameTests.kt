package pt.isel.ls.sessions.http

import junit.framework.TestCase
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import pt.isel.ls.sessions.http.model.game.GameDTO
import pt.isel.ls.sessions.http.model.game.GameInputModel
import pt.isel.ls.sessions.http.model.utils.MessageResponse
import pt.isel.ls.sessions.http.routes.game.GameRouter
import pt.isel.ls.sessions.http.util.APPLICATION_JSON
import pt.isel.ls.sessions.http.util.CONTENT_TYPE
import pt.isel.ls.sessions.http.util.ProblemDTO
import pt.isel.ls.sessions.http.util.Uris
import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.sessions.services.game.GameService
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameTests {
    @AfterTest
    fun clear() {
        mem.gameDB.reset()
    }

    private fun createGame() {
        games.forEach {
            router.routes(
                Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(it))
                    .header("Authorization", "Bearer token"),
            )
        }
    }

    @Test
    fun `game created with success`() {
        //  Arrange
        val game = GameInputModel("cod", "developer", listOf("Action", "Shooter"))
        val request =
            Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(game)).header("Authorization", "Bearer token")

        // Act
        val response = router.routes(request)
        val content = Json.decodeFromString<MessageResponse>(response.bodyString())
        // Assert
        assertEquals("/game/1", response.header("Location"))
        assertTrue { response.status.successful }
        assertEquals(Status.CREATED, response.status)
        assertTrue { content.message.isNotBlank() }
        assertEquals("Game created: 1", content.message)
    }

    @Test
    fun `game creation fails because name already exists`() {
        // Arrange
        val game = GameInputModel("cod", "developer", listOf("Action", "Shooter"))
        val request =
            Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(game)).header("Authorization", "Bearer token")
        // Act
        router.routes(request)
        val response = router.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(content.status, Status.BAD_REQUEST.code)
        assertTrue(content.title.isNotBlank())
        assertEquals("Game name already exists", content.title)
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/game-name-already-exists",
            content.type,
        )
    }

    @Test
    fun `game creation fails because no genre is passed`() {
        // Arrange
        val request =
            Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(gameNoGenres))
                .header("Authorization", "Bearer token")
        // Act
        val response = router.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals("application/problem+json", response.header("Content-Type"))
        assertTrue { response.status.clientError }
        assertEquals(Status.BAD_REQUEST, response.status)
        assertEquals("Invalid game data", content.title)
        assertEquals("Invalid game data", content.detail)
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/invalid-game-data",
            content.type,
        )
    }

    @Test
    fun `game creation fails because genre is invalid`() {
        // Arrange
        val request =
            Request(Method.POST, Uris.DEFAULT).body(Json.encodeToString(gameInvalidGenre))
                .header("Authorization", "Bearer token")
        // Act
        val response = router.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.NOT_FOUND.code, content.status)
        assertEquals("Genre not found", content.title)
        assertEquals("Genre or genres not found", content.detail)
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/genre-not-found",
            content.type,
        )
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
        assertEquals(Status.OK, response.status)
        assertEquals(APPLICATION_JSON, response.header(CONTENT_TYPE))
        assertEquals(game.name, content.name)
        assertEquals(game.developer, content.developer)
        assertEquals(game.genres, content.genres)
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
        assertEquals(Status.NOT_FOUND, response.status)
        assertEquals("application/problem+json", response.header(CONTENT_TYPE))
        assertEquals("Game not found", content.title)
        assertEquals("Game with given id not found", content.detail)
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/game-not-found",
            content.type,
        )
    }

    @Test
    fun `get all games with success`() {
        // Arrange
        createGame()
        val request =
            Request(Method.GET, Uris.DEFAULT).query("developer", "developer").query(
                "genres",
                "Action",
            ).header("Authorization", "Bearer token")
        // Act
        val response = router.routes(request)
        val content = Json.decodeFromString<List<GameDTO>>(response.bodyString())
        // Assert
        assertTrue { response.status.successful }
        assertTrue { response.header(CONTENT_TYPE) == APPLICATION_JSON }
        assertTrue { content.size == 3 }
        assertFalse { content.contains(gamesResult[1]) }
    }

    @Test
    fun `get all games success despite no games being returned`() {
        // Arrange
        createGame()
        val request =
            Request(
                Method.GET,
                Uris.DEFAULT,
            ).query("developer", "developer").query(
                "genres",
                "Rpg",
            ).header("Authorization", "Bearer token")
        // Act
        val response = router.routes(request)
        val content = Json.decodeFromString<List<GameDTO>>(response.bodyString())
        // Assert
        assertEquals(Status.OK, response.status)
        assertEquals(APPLICATION_JSON, response.header(CONTENT_TYPE))
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
            ).query("developer", "developer3").query(
                "genres",
                "Action",
            )
        // Act
        val response = router.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.NOT_FOUND, response.status)
        assertEquals("application/problem+json", response.header(CONTENT_TYPE))
        assertEquals("Developer not found", content.title)
        assertEquals("Developer not found", content.detail)
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/developer-not-found",
            content.type,
        )
    }

    @Test
    fun `get all games fails because genres doesn't exist`() {
        // Arrange
        createGame()
        val request =
            Request(Method.GET, Uris.DEFAULT).query("developer", "developer3").query(
                "genres",
                "Friendly,Co-op",
            )
        // Act
        val response = router.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.NOT_FOUND, response.status)
        assertEquals("application/problem+json", response.header(CONTENT_TYPE))
        assertEquals("Genre not found", content.title)
        assertEquals("Genre or genres not found", content.detail)
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/genre-not-found",
            content.type,
        )
    }

    @Test
    fun `get all games fails because developer not found`() {
        // Arrange
        createGame()
        val request =
            Request(Method.GET, Uris.DEFAULT).query("developer", "d").query(
                "genres",
                "sports,multiplayer",
            )
        // Act
        val response = router.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.NOT_FOUND, response.status)
        assertEquals("application/problem+json", response.header(CONTENT_TYPE))
        assertEquals("Developer not found", content.title)
        assertEquals("Developer not found", content.detail)
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/developer-not-found",
            content.type,
        )
    }

    @Test
    fun `get all game success with skip(DEFAULT) and limit`() {
        // Arrange
        createGame()
        val request =
            Request(Method.GET, Uris.DEFAULT).query("limit", "2").query("developer", "developer").query(
                "genres",
                "Action",
            )
        // Act
        val response = router.routes(request)
        val content = Json.decodeFromString<List<GameDTO>>(response.bodyString())
        // Assert
        assertEquals(Status.OK, response.status)
        assertEquals(APPLICATION_JSON, response.header(CONTENT_TYPE))
        assertEquals(2, content.size)
        assertEquals(gamesResult[0], content[0])
        assertEquals(gamesResult[2], content[1])
        assertFalse { content.contains(gamesResult[1]) }
        assertFalse(content.contains(gamesResult[3]))
    }

    @Test
    fun `get all game success with skip and limit(DEFAULT)`() {
        // Arrange
        createGame()
        val request =
            Request(Method.GET, Uris.DEFAULT).query("skip", "1").query("developer", "developer").query(
                "genres",
                "Action",
            )
        // Act
        val response = router.routes(request)
        val content = Json.decodeFromString<List<GameDTO>>(response.bodyString())
        // Assert
        assertEquals(Status.OK, response.status)
        assertEquals(APPLICATION_JSON, response.header(CONTENT_TYPE))
        assertEquals(2, content.size)
        assertEquals(gamesResult[2], content[0])
        assertEquals(gamesResult[3], content[1])
        assertFalse { content.contains(gamesResult[0]) }
        assertFalse { content.contains(gamesResult[1]) }
    }

    @Test
    fun `get all game success with skip and limit`() {
        // Arrange
        createGame()
        val request =
            Request(Method.GET, Uris.DEFAULT).query("limit", "3").query("skip", "0").query("developer", "developer")
                .query(
                    "genres",
                    "Action",
                )
        // Act
        val response = router.routes(request)
        val content = Json.decodeFromString<List<GameDTO>>(response.bodyString())
        // Assert
        assertEquals(Status.OK, response.status)
        assertEquals(APPLICATION_JSON, response.header(CONTENT_TYPE))
        assertEquals(3, content.size)
        assertContains(content, gamesResult[0])
        assertContains(content, gamesResult[2])
        assertContains(content, gamesResult[3])
    }

    @Test
    fun `get all game fails with skip negative`() {
        // Arrange
        createGame()
        val skip = -1
        val request =
            Request(Method.GET, Uris.DEFAULT).query("skip", "$skip").query("developer", "developer").query(
                "genres",
                "Action",
            )
        // Act
        val response = router.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.BAD_REQUEST, response.status)
        assertEquals("application/problem+json", response.header(CONTENT_TYPE))
        assertEquals("Invalid skip or limit", content.title)
        assertEquals("Invalid skip or limit", content.detail)
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/invalid-skip-or-limit",
            content.type,
        )
        assertEquals("?skip=$skip&developer=developer&genres=Action", content.instance)
    }

    @Test
    fun `get all game fails with limit negative`() {
        // Arrange
        createGame()
        val limit = -1
        val request =
            Request(Method.GET, Uris.DEFAULT).query("limit", "$limit").query("developer", "developer").query(
                "genres",
                "Action",
            )
        // Act
        val response = router.routes(request)
        val content = Json.decodeFromString<ProblemDTO>(response.bodyString())
        // Assert
        assertEquals(Status.BAD_REQUEST, response.status)
        assertEquals("application/problem+json", response.header(CONTENT_TYPE))
        assertEquals("Invalid skip or limit", content.title)
        assertEquals("Invalid skip or limit", content.detail)
        assertEquals(
            "https://github.com/isel-leic-ls/2324-2-LEIC42D-G04/tree/main/docs/problems/invalid-skip-or-limit",
            content.type,
        )
        assertEquals("?limit=$limit&developer=developer&genres=Action", content.instance)
    }

    companion object {
        private val clock = Clock.System
        private val mem = AppMemoryDB(clock)
        private val service = GameService(mem.gameDB)
        private val router = GameRouter(service)
        private val game = GameInputModel("cod", "developer", listOf("Action", "Shooter"))
        private val gameInvalidGenre = GameInputModel("cod", "developer", listOf("multiplayer", "sport", "Action"))
        private val gameNoGenres = GameInputModel("cod", "developer", listOf())
        private val gamesResult =
            listOf(
                GameDTO(1u, "cod", "developer", listOf("Action", "Shooter")),
                GameDTO(2u, "cs-go", "developer1", listOf("Action", "Shooter")),
                GameDTO(3u, "over-watch", "developer", listOf("Action", "Shooter")),
                GameDTO(4u, "FIFA", "developer", listOf("Action", "Sports")),
            )

        private val games = gamesResult.map { GameInputModel(it.name, it.developer, it.genres) }
    }
}
