package pt.isel.ls.sessions.repository


import junit.framework.TestCase.assertEquals
import pt.isel.ls.sessions.repository.data.player.PlayerMemoryDB
import kotlin.test.Test
import kotlin.test.assertFailsWith

class PlayerTests {

    @Test
    fun `create player`() {
        val token = gameMemoryDB.createPlayer(NAME, EMAIL)
        val player = gameMemoryDB.getPlayers().first()
        assertEquals(NAME, player.name)
        assertEquals(EMAIL, player.email.value)
        assertEquals(token, token)
    }

    @Test
    fun `get players`() {
        gameMemoryDB.createPlayer(NAME, EMAIL)
        val players = gameMemoryDB.getPlayers()
        assertEquals(4, players.size)
        assertEquals(NAME, players.first().name)
        assertEquals(EMAIL, players.first().email.value)
    }

    @Test
    fun `get player by id`() {
        val token = gameMemoryDB.createPlayer(NAME, EMAIL)
        val player = gameMemoryDB.getPlayerById(token.pid)
        assertEquals(NAME, player?.name)
        assertEquals(EMAIL, player?.email?.value)
    }

    @Test
    fun `get player by id not found`() {
        val player = gameMemoryDB.getPlayerById(0u)
        assertEquals(null, player)
    }

    @Test
    fun `IllegalStateException email in use`() {
        val email = "asda"
        assertFailsWith<IllegalArgumentException> {
            gameMemoryDB.createPlayer(NAME, email)
        }
    }

    @Test
    fun isEmailInUse() {
        gameMemoryDB.createPlayer(NAME, EMAIL)
        val isEmailInUse = gameMemoryDB.isEmailInUse(EMAIL)
        assertEquals(true, isEmailInUse)
    }

    @Test
    fun isEmailInUseFalse() {
        val isEmailInUse = gameMemoryDB.isEmailInUse("teste")
        assertEquals(false, isEmailInUse)
    }

    private companion object {
        val gameMemoryDB = PlayerMemoryDB()
        const val NAME = "name"
        const val EMAIL = "email@gmai.com"

    }

}