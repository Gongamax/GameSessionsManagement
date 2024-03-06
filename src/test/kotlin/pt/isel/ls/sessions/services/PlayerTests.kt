package pt.isel.ls.sessions.services

import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.sessions.services.player.PlayerService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PlayerTests {

    private val baseDate = AppMemoryDB()
    private val name = "Francisco"
    private val email = "francisco@gmail.com"

    init {
        baseDate.playerMemoryDB.createPlayer(name, email)
    }

    @Test
    fun createPlayer() {
        // arrange
        val playerService = PlayerService(baseDate)
        val nPlayer = "Alice"
        val ePlayer = "alice@email.com"
        // act
        val player = playerService.createPlayer(nPlayer, ePlayer)
        val pid = player.pid
        val namePlayer = baseDate.playerMemoryDB.getPlayerById(pid)?.name
        val emailPlayer = baseDate.playerMemoryDB.getPlayerById(pid)?.email
        // assert
        assertEquals(nPlayer, namePlayer)
        assertEquals(emailPlayer, emailPlayer)
        assertEquals(pid, player.pid)
    }

    @Test
    fun getDetailsPlayer() {
        // arrange
        val playerService = PlayerService(baseDate)
        val pid = 1
        // arrange
        val player = playerService.getDetailsPlayer(pid)
        // assert
        assertEquals(pid, player?.pid)
        assertEquals(name, player?.name)
        assertEquals(email, player?.email)
    }

    @Test
    fun getDetailsPlayerNotFound() {
        // arrange
        val playerService = PlayerService(baseDate)
        val pid = 34
        // arrange
        val player = playerService.getDetailsPlayer(pid)
        // assert
        assertEquals(null, player)
    }

    @Test
    fun `create player email already exists`() {
        // arrange
        val playerService = PlayerService(baseDate)
        val nPlayer = "Bob"
        // arrange
        assertFailsWith<IllegalArgumentException> {
            playerService.createPlayer(nPlayer, email)
        }
    }




}