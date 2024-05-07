package pt.isel.ls.sessions.services.player

import pt.isel.ls.sessions.domain.player.Email
import pt.isel.ls.sessions.repository.PlayerRepository
import pt.isel.ls.utils.failure
import pt.isel.ls.utils.success

class PlayerService(private val playerRepository: PlayerRepository) {
    fun createPlayer(
        name: String,
        email: String,
    ): PlayerCreationResult =
        run {
            if (!Email.isValidEmail(email)) {
                return@run failure(PlayerCreationError.InvalidEmail)
            }
            if (playerRepository.isEmailInUse(email)) {
                return@run failure(PlayerCreationError.EmailExists)
            }
            if (playerRepository.isNameInUse(name)) {
                return@run failure(PlayerCreationError.NameExists)
            }
            val token = playerRepository.createPlayer(name, email)
            success(token)
        }

    fun getDetailsPlayer(pid: UInt): PlayerGetResult =
        run {
            val player = playerRepository.getPlayerById(pid) ?: return@run failure(PlayerGetError.PlayerNotFound)
            success(player)
        }

    fun searchPlayers(
        name: String,
        limit: Int,
        skip: Int,
    ): PlayerSearchResult = playerRepository.searchPlayers(name, limit, skip)
}
