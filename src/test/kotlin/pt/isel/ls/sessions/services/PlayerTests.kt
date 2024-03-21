package pt.isel.ls.sessions.services

import junit.framework.TestCase.assertEquals
import kotlinx.datetime.Clock
import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.sessions.services.player.PlayerCreationError
import pt.isel.ls.sessions.services.player.PlayerGetError
import pt.isel.ls.sessions.services.player.PlayerService
import pt.isel.ls.utils.Failure
import pt.isel.ls.utils.Success
import pt.isel.ls.utils.failure
import kotlin.test.Test
import kotlin.test.assertNotNull

class PlayerTests {
    private val clock = Clock.System
    private val baseDate = AppMemoryDB(clock)
    private val name = "Francisco"
    private val email = "francisco@gmail.com"

    init {
        baseDate.playerDB.createPlayer(name, email)
    }

    @Test
    fun createPlayer() {
        // arrange
        val playerService = PlayerService(baseDate.playerDB)
        val nPlayer = "Alice"
        val ePlayer = "alice@email.com"
        // act
        val pid: UInt? =
            when (val res = playerService.createPlayer(nPlayer, ePlayer)) {
                is Success -> res.value.pid
                is Failure -> null
            }
        assertNotNull(pid)
        val namePlayer = baseDate.playerDB.getPlayerById(pid)?.name
        val emailPlayer = baseDate.playerDB.getPlayerById(pid)?.email

        // assert
        assertEquals(nPlayer, namePlayer)
        assertEquals(emailPlayer, emailPlayer)
    }

    @Test
    fun getDetailsPlayer() {
        // arrange
        val playerService = PlayerService(baseDate.playerDB)
        val pid = 1u
        // arrange
        val player =
            when (val res = playerService.getDetailsPlayer(pid)) {
                is Success -> res.value
                is Failure -> null
            }

        // assert
        assertNotNull(player)
        assertEquals(pid, player.pid)
        assertEquals(name, player.name)
        assertEquals(email, player.email.value)
    }

    @Test
    fun getDetailsPlayerNotFound() {
        // arrange
        val playerService = PlayerService(baseDate.playerDB)
        val pid = 34u
        // arrange
        val player = playerService.getDetailsPlayer(pid)
        // assert
        assertEquals(failure(PlayerGetError.PlayerNotFound), player)
    }

    @Test
    fun `create player email already exists`() {
        // arrange
        val playerService = PlayerService(baseDate.playerDB)
        val nPlayer = "Bob"
        val player = playerService.createPlayer(nPlayer, email)
        // arrange
        assertEquals(failure(PlayerCreationError.EmailExists), player)
    }
}
