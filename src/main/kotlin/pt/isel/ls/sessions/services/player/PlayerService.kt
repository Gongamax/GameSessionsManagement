package pt.isel.ls.sessions.services.player

import pt.isel.ls.sessions.domain.utils.Token
import pt.isel.ls.sessions.repository.data.player.PlayerDB
import pt.isel.ls.utils.failure
import pt.isel.ls.utils.success

class PlayerService(private val playerRepository: PlayerDB) {

    fun createPlayer(name: String, email: String): PlayerCreationResult = run {
        if (playerRepository.isEmailInUse(email))
            return@run failure(PlayerCreationError.EmailExists)
        val token = playerRepository.createPlayer(name, email)
        success(token)
    }

    fun getDetailsPlayer(pid: UInt): PlayerGetResult = run {
        val player = playerRepository.getPlayerById(pid) ?: return@run failure(PlayerGetError.PlayerNotFound)
        success(player)
    }
}