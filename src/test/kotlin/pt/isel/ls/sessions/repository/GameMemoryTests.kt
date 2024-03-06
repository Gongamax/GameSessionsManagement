package pt.isel.ls.sessions.repository

import pt.isel.ls.sessions.domain.game.Genres
import pt.isel.ls.sessions.repository.data.game.GameMemoryDB
import kotlin.test.AfterTest
import kotlin.test.Test

class GameMemoryTests {

    @AfterTest
    fun cleanup() {
        gameMemoryDB.reset()
    }

    @Test
    fun testCreateGame() {
        // Arrange
        // Act
        val id = gameMemoryDB.createGame(NAME, DEVELOPER, genres)
        // Assert
        assert(id != null)
    }

    @Test
    fun testCreateGameWithSameName() {
        // Arrange
        // Act
        val id = gameMemoryDB.createGame(NAME, DEVELOPER, genres)
        val id2 = gameMemoryDB.createGame(NAME, DEVELOPER, genres)
        // Assert
        assert(id != null)
        assert(id2 == null)
    }

    @Test
    fun testGetGameById() {
        // Arrange
        // Act
        val id = gameMemoryDB.createGame(NAME, DEVELOPER, genres)
        val game = gameMemoryDB.getGameById(id!!)
        // Assert
        assert(game != null)
    }

    @Test
    fun testGetGames() {
        // Arrange
        // Act
        // Assert
        //cleanup
    }

    @Test
    fun testGetGamesFails() {
        // Arrange
        // Act
        // Assert
        //cleanup
    }

    private companion object{
        val gameMemoryDB = GameMemoryDB()
        const val NAME = "name"
        const val DEVELOPER = "developer"
        val genres = listOf(Genres.RPG, Genres.ACTION)
    }
}
