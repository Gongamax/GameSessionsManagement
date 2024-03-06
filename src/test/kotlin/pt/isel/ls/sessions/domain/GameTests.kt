package pt.isel.ls.sessions.domain

import pt.isel.ls.sessions.domain.game.*
import pt.isel.ls.sessions.domain.game.toGenre
import kotlin.test.*

class GameTests {

    @Test
    fun testCreateGame() {
        //Arrange
        val game = Game(1, "name", "Ubisoft", listOf(Genres.RPG, Genres.ADVENTURE))
        //Act
        //Assert
        assertEquals(1, game.gid)
        assertEquals("name", game.name)
        assertEquals("Ubisoft", game.developer)
        assertTrue { game.genres.contains(Genres.RPG) }
    }

    @Test
    fun `test Game toString`() {
        //Arrange
        val game = Game(1, "name", "Ubisoft", listOf(Genres.RPG, Genres.ADVENTURE))
        //Act
        //Assert
        assertEquals("Game(gid=1, name=name, developer=Ubisoft, genres=[RPG, ADVENTURE])", game.toString())
    }

    @Test
    fun `test Game Genres`() {
        //Arrange
        val genre = "RPG"
        //Act
        //Assert
        assertTrue { genre.toGenre() == Genres.RPG }
    }

    @Test
    fun `test Game Genres with invalid genre`() {
        //Arrange
        //Act
        //Assert
        assertNull("Invalid".toGenre())
    }
}