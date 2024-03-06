package pt.isel.ls.sessions.services.player

import pt.isel.ls.sessions.repository.data.AppMemoryDB
import pt.isel.ls.sessions.repository.data.player.PlayerMemoryDB


class PlayerService(private val memoryDB: AppMemoryDB){



    fun createPlayer(name:String, email: String) = memoryDB.playerMemoryDB.createPlayer(name, email)

    fun getDetailsPlayer(pid: Int) = memoryDB.playerMemoryDB.getPlayerById(pid)



}