package pt.isel.ls.sessions.services

import junit.framework.TestCase.assertEquals
import kotlinx.datetime.Clock
import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.sessions.services.game.GameService
import pt.isel.ls.sessions.services.game.GamesGetError
import pt.isel.ls.utils.Either
import pt.isel.ls.utils.Failure
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.fail

class GameTests {
    @AfterTest
    fun cleanup() {
        appMemoryDB.gameDB.reset()
    }

    @Test
    fun createGame() {
        // Arrange
        // Act
        val result =
            when (val value = gameService.createGame(NAME, DEVELOPER, genres)) {
                is Either.Left -> fail("Game creation failed for $value")
                is Either.Right -> value.value
            }
        // Assert
        assertEquals(1u, result)
    }

    @Test
    fun createGameWithSameName() {
        // Arrange
        // Act
        val id =
            when (val value = gameService.createGame(NAME, DEVELOPER, genres)) {
                is Either.Left -> fail("Game creation failed for $value")
                is Either.Right -> value.value
            }
        val id2 =
            when (val value = gameService.createGame(NAME, DEVELOPER, genres)) {
                is Either.Left -> null
                is Either.Right -> value.value
            }
        // Assert
        assertEquals(1u, id)
        assertEquals(null, id2)
    }

    @Test
    fun searchGamesByName() {
        // Arrange
        when (val value = gameService.createGame(NAME, DEVELOPER, genres)) {
            is Either.Left -> fail("Game creation failed for $value")
            is Either.Right -> value.value
        }
        // Act
        val result =
            when (val value = gameService.searchGamesByName("nam", 10, 0)) {
                is Either.Left -> fail("Game search failed for $value")
                is Either.Right -> value.value
            }
        // Assert
        assertEquals(1, result.size)
        assertEquals(NAME, result.first().name)
    }

    @Test
    fun testGetGame() {
        // Arrange
        val gid =
            when (val value = gameService.createGame(NAME, DEVELOPER, genres)) {
                is Either.Left -> fail("Game creation failed for $value")
                is Either.Right -> value.value
            }
        // Act
        val result =
            when (val value = gameService.getGame(gid)) {
                is Either.Left -> fail("Game retrieval failed for $value")
                is Either.Right -> value.value
            }
        // Assert
        assertEquals(NAME, result.name)
    }

    @Test
    fun testGetNameThatDoesNotExist() {
        // Arrange
        val gid = 1
        // Act
        val result =
            when (val value = gameService.getGame(gid.toUInt())) {
                is Either.Left -> null
                is Either.Right -> value.value
            }
        // Assert
        assertEquals(null, result)
    }

    @Test
    fun testGetGames() {
        // Arrange

        when (val value = gameService.createGame(NAME, DEVELOPER, genres)) {
            is Either.Left -> fail("Game creation failed for $value")
            is Either.Right -> value.value
        }
        // Act
        val result =
            when (val value = gameService.getGames(genres, DEVELOPER, 10, 0)) {
                is Either.Left -> fail("Game retrieval failed for $value")
                is Either.Right -> value.value
            }
        // Assert
        assertEquals(1, result.size)
        assertEquals(NAME, result.first().name)
    }

    @Test
    fun `getGamesFails with GenreNotFound`() {
        // Arrange
        gameService.createGame(NAME, DEVELOPER, genres)
        // Act
        val result = gameService.getGames(listOf("rpg", "Action", "Unknown"), DEVELOPER, 10, 0)
        // Assert
        assertEquals(Failure(GamesGetError.GenreNotFound), result)
    }

    @Test
    fun `getGamesFails with DeveloperNotFound`() {
        // Arrange
        gameService.createGame(NAME, DEVELOPER, genres)
        // Act
        val result = gameService.getGames(genres, "Unknown", 10, 0)
        // Assert
        assertEquals(Failure(GamesGetError.DeveloperNotFound), result)
    }

    private companion object {
        private val clock: Clock = Clock.System
        val appMemoryDB = AppMemoryDB(clock)
        val gameService = GameService(appMemoryDB.gameDB)
        const val NAME = "name"
        const val DEVELOPER = "developer"
        val genres = listOf("rpg", "Action")
    }
}
