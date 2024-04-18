package pt.isel.ls.sessions.domain

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import pt.isel.ls.sessions.domain.game.Game
import pt.isel.ls.sessions.domain.game.Genres
import pt.isel.ls.sessions.domain.game.toGenre
import kotlin.test.Test
import kotlin.test.assertTrue

class GameTests {
    @Test
    fun testCreateGame() {
        // Arrange
        val game = Game(1u, "name", "Ubisoft", listOf(Genres.RPG, Genres.ADVENTURE))
        // Act
        // Assert
        assertEquals(1u, game.gid)
        assertEquals("name", game.name)
        assertEquals("Ubisof", game.developer)
        assertTrue { game.genres.contains(Genres.RPG) }
    }

    @Test
    fun `test Game toString`() {
        // Arrange
        val game = Game(1u, "name", "Ubisoft", listOf(Genres.RPG, Genres.ADVENTURE))
        // Act
        // Assert
        assertEquals("Game(gid=1, name=name, developer=Ubisoft, genres=[RPG, ADVENTURE])", game.toString())
    }

    @Test
    fun `test Game Genres`() {
        // Arrange
        val genre = "RPG"
        // Act
        // Assert
        assertTrue { genre.toGenre() == Genres.RPG }
    }

    @Test
    fun `test Game Genres with invalid genre`() {
        // Arrange
        // Act
        // Assert
        assertNull("Invalid".toGenre())
    }
}
