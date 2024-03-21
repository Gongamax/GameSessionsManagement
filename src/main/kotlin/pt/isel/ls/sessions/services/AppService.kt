package pt.isel.ls.sessions.services

import kotlinx.datetime.Clock
import pt.isel.ls.sessions.repository.AppDB
import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.sessions.services.game.GameService
import pt.isel.ls.sessions.services.player.PlayerService
import pt.isel.ls.sessions.services.session.SessionService

class AppService(memoryDB: AppDB) {

    private val baseData = memoryDB
    private val clock = Clock.System

    val playerService = PlayerService(baseData.playerDB)

    val gameService = GameService(baseData)

    val sessionService = SessionService(baseData.sessionDB, baseData.playerDB, baseData.gameDB, clock)

    fun reset() {
        baseData.playerDB.reset()
    }
}