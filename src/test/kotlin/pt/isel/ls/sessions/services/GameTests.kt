package pt.isel.ls.sessions.services

import junit.framework.TestCase.assertEquals
import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.sessions.services.game.GameService
import pt.isel.ls.utils.Either
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
        val result = when (val value = gameService.createGame(NAME, DEVELOPER, genres)) {
            is Either.Left -> fail("Game creation failed for $value")
            is Either.Right -> value.value
        }
        // Assert
        assertEquals(1, result)
    }

    @Test
    fun createGameWithSameName() {
        // Arrange
        // Act
        val id = when (val value = gameService.createGame(NAME, DEVELOPER, genres)) {
            is Either.Left -> fail("Game creation failed for $value")
            is Either.Right -> value.value
        }
        val id2 = when (val value = gameService.createGame(NAME, DEVELOPER, genres)) {
            is Either.Left -> null
            is Either.Right -> value.value
        }
        // Assert
        assertEquals(1, id)
        assertEquals(null, id2)
    }

    @Test
    fun testGetGame() {
        // Arrange
        val gid = when (val value = gameService.createGame(NAME, DEVELOPER, genres)) {
            is Either.Left -> fail("Game creation failed for $value")
            is Either.Right -> value.value
        }
        // Act
        val result = when (val value = gameService.getGame(gid)) {
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
        val result = when (val value = gameService.getGame(gid.toUInt())) {
            is Either.Left -> null
            is Either.Right -> value.value
        }
        // Assert
        assertEquals(null, result)
    }

    @Test
    fun testGetGames() {
        // Arrange
        // Act
        // Assert
        TODO("Not yet implemented... Saraiva!!!")
    }

    @Test
    fun testGetGamesFails() {
        // Arrange
        // Act
        // Assert
        TODO("Not yet implemented... Saraiva!!!")
    }

    private companion object {
        val appMemoryDB = AppMemoryDB()
        val gameService = GameService(appMemoryDB)
        const val NAME = "name"
        const val DEVELOPER = "developer"
        val genres = listOf("rpg", "Action")
    }
}