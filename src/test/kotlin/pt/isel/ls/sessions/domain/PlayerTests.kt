package pt.isel.ls.sessions.domain

import pt.isel.ls.sessions.domain.player.Email
import pt.isel.ls.sessions.domain.player.Player
import kotlin.test.*

class PlayerTests {

    @Test
    fun testCreatePlayer() {
        val player = Player(1u, "name", Email("email"))
        assertEquals(1u, player.pid)
        assertEquals("name", player.name)
        assertEquals("email", player.email.value)
    }

    @Test
    fun `test Player toString`() {
        val player = Player(1u, "name", Email("email"))
        assertEquals("Player(pid=1, name=name, email=email)", player.toString())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test invalid email`() {
        Player(1u, "name", Email("email"))
    }

    @Test
    fun `test isValidEmail on a Player`() {
        val player2 = Player(1u, "name", Email("email@isel.pt"))
        assertTrue { Email.isValidEmail(player2.email.value) }
    }

    companion object {
        private const val mail = "email"
    }
}