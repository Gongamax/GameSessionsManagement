package pt.isel.ls.sessions.services.player

import pt.isel.ls.sessions.repository.data.AppMemoryDB

class PlayerService(private val memoryDB: AppMemoryDB) {

    fun createPlayer(name: String, email: String) = memoryDB.playerMemoryDB.createPlayer(name, email)

    fun getDetailsPlayer(pid: UInt) = memoryDB.playerMemoryDB.getPlayerById(pid)
}