package pt.isel.ls.sessions.services

import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.sessions.services.game.GameService
import pt.isel.ls.sessions.services.player.PlayerService
import pt.isel.ls.sessions.services.session.SessionService

class AppService(memoryDB: AppMemoryDB) {

    private val baseData = memoryDB

    val playerService = PlayerService(baseData)

    val gameService = GameService(baseData)

    val sessionService = SessionService(baseData.sessionMemoryDB, playerService, gameService)

    fun reset() {
        baseData.playerMemoryDB.reset()
    }
}