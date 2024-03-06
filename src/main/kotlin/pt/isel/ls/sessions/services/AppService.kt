package pt.isel.ls.sessions.services

import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.sessions.services.player.PlayerService

class AppService(memoryDB: AppMemoryDB) {

    private val baseData = memoryDB

    val playerService = PlayerService(baseData)

    fun reset() {
        baseData.playerMemoryDB.reset()
    }


}