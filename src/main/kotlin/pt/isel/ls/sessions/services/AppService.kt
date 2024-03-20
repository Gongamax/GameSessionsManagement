package pt.isel.ls.sessions.services

import kotlinx.datetime.Clock
import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.sessions.services.game.GameService
import pt.isel.ls.sessions.services.player.PlayerService
import pt.isel.ls.sessions.services.session.SessionService

class AppService(memoryDB: AppMemoryDB) {

    private val baseData = memoryDB
    private val clock = Clock.System

    val playerService = PlayerService(baseData.playerMemoryDB)

    val gameService = GameService(baseData)

    val sessionService = SessionService(baseData.sessionMemoryDB, baseData.playerMemoryDB, baseData.gameMemoryDB, clock)

    fun reset() {
        baseData.playerMemoryDB.reset()
    }
}