package pt.isel.ls.sessions.services

import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.sessions.services.game.GameService
import pt.isel.ls.sessions.services.player.PlayerService

class AppService(memoryDB: AppMemoryDB) {

    private val baseData = memoryDB

    val playerService = PlayerService(baseData)

    val gameService = GameService(baseData)

    fun reset() {
        baseData.playerMemoryDB.reset()
    }


}