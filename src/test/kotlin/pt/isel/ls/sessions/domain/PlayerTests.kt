package pt.isel.ls.sessions.domain

import pt.isel.ls.sessions.domain.player.Player
import kotlin.test.*

class PlayerTests {

    @Test
    fun testCreatePlayer() {
        val player = Player(1u, "name", "email")
        assertEquals(1u, player.pid)
        assertEquals("name", player.name)
        assertEquals("email", player.email)
    }

    @Test
    fun `test Player toString`() {
        val player = Player(1u, "name", "email")
        assertEquals("Player(pid=1, name=name, email=email)", player.toString())
    }

    @Test
    fun `test isValidEmail on a Player`() {
        val player = Player(1u, "name", "email")
        assertFalse(Player.isValidEmail(player.email))

        val player2 = Player(1u, "name", "email@isel.pt")
        assertTrue(Player.isValidEmail(player2.email))
    }
}