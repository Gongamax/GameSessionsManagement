package pt.isel.ls.sessions.repository

import pt.isel.ls.sessions.domain.game.Genres
import pt.isel.ls.sessions.repository.data.game.GameMemoryDB
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GameTests {
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
        assertEquals(1u, id)
    }

    @Test
    fun testGetGameById() {
        // Arrange
        // Act
        val id = gameMemoryDB.createGame(NAME, DEVELOPER, genres)
        val game = gameMemoryDB.getGameById(id)
        // Assert
        assertEquals(id, game?.gid)
        assertEquals(NAME, game?.name)
        assertEquals(DEVELOPER, game?.developer)
        assertEquals(genres, game?.genres)
    }

    @Test
    fun testGetGames() {
        // Arrange
        val id = gameMemoryDB.createGame(NAME, DEVELOPER, genres)
        // Act
        val games = gameMemoryDB.getGames(genres, DEVELOPER, 10, 0)
        // Assert
        assertEquals(1, games.size)
        assertEquals(id, games[0].gid)
        assertEquals(NAME, games[0].name)
        assertEquals(DEVELOPER, games[0].developer)
        assertEquals(genres, games[0].genres)
    }

    @Test
    fun testGetDeveloperByName() {
        // Arrange
        val id = gameMemoryDB.createGame(NAME, DEVELOPER, genres)
        // Act
        val developer = gameMemoryDB.getDeveloperByName(DEVELOPER)
        // Assert
        assertEquals(DEVELOPER, developer)
    }

    @Test
    fun testGetGamesFails() {
        // Arrange
        val id = gameMemoryDB.createGame(NAME, DEVELOPER, genres)
        // Act
        val games = gameMemoryDB.getGames(genres, "another developer", 10, 0)
        // Assert
        assertEquals(0, games.size)
    }

    @Test
    fun `get Games with limit and skip`() {
        // Arrange
        val id = gameMemoryDB.createGame(NAME, DEVELOPER, genres)
        // Act
        val games = gameMemoryDB.getGames(genres, DEVELOPER, 1, 0)
        // Assert
        assertEquals(1, games.size)
    }

    private companion object {
        val gameMemoryDB = GameMemoryDB()
        const val NAME = "name"
        const val DEVELOPER = "developer"
        val genres = listOf(Genres.RPG, Genres.ACTION)
    }
}
