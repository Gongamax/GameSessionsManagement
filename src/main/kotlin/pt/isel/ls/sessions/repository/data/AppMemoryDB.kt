package pt.isel.ls.sessions.repository.data

import pt.isel.ls.sessions.repository.data.game.GameMemoryDB
import pt.isel.ls.sessions.repository.data.player.PlayerMemoryDB

class AppMemoryDB {

    val playerMemoryDB = PlayerMemoryDB()
    val gameMemoryDB = GameMemoryDB()

}