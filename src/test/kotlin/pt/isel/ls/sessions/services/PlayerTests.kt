package pt.isel.ls.sessions.services

import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.sessions.services.player.PlayerService
import pt.isel.ls.utils.Failure
import pt.isel.ls.utils.Success
import pt.isel.ls.utils.failure
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

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
        val playerService = PlayerService(baseDate.playerMemoryDB)
        val nPlayer = "Alice"
        val ePlayer = "alice@email.com"
        // act
        val pid: UInt? = when (val res = playerService.createPlayer(nPlayer, ePlayer)) {
            is Success -> res.value.pid
            is Failure -> null
        }
        assertNotNull(pid)
        val namePlayer = baseDate.playerMemoryDB.getPlayerById(pid)?.name
        val emailPlayer = baseDate.playerMemoryDB.getPlayerById(pid)?.email

        // assert
        assertEquals(nPlayer, namePlayer)
        assertEquals(emailPlayer, emailPlayer)
    }

    @Test
    fun getDetailsPlayer() {
        // arrange
        val playerService = PlayerService(baseDate.playerMemoryDB)
        val pid = 1u
        // arrange
        val player = when (val res = playerService.getDetailsPlayer(pid)) {
            is Success -> res.value
            is Failure -> null
        }

        // assert
        assertNotNull(player)
        assertEquals(pid, player.pid)
        assertEquals(name, player.name)
        assertEquals(email, player.email)
    }

//    @Test
//    fun getDetailsPlayerNotFound() {
//        // arrange
//        val playerService = PlayerService(baseDate.playerMemoryDB)
//        val pid = 34u
//        // arrange
//        val player = playerService.getDetailsPlayer(pid)
//        // assert
//        assertEquals(null, player)
//    }
//
//    @Test
//    fun `create player email already exists`() {
//        // arrange
//        val playerService = PlayerService(baseDate.playerMemoryDB)
//        val nPlayer = "Bob"
//        // arrange
//        assertFailsWith<IllegalArgumentException> {
//            playerService.createPlayer(nPlayer, email)
//        }
//    }

}