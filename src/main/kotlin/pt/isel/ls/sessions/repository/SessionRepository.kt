package pt.isel.ls.sessions.repository

import kotlinx.datetime.LocalDateTime
import pt.isel.ls.sessions.domain.player.Player
import pt.isel.ls.sessions.domain.session.Session
import pt.isel.ls.sessions.domain.session.SessionState

interface SessionRepository {

    fun createSession(capacity : Int, gid : Int, date : LocalDateTime) : Int

    fun addPlayerToSession(sid : Int, player: Player)

    fun getSession(sid : Int) : Session?

    fun getSessions(gid : Int, date : LocalDateTime?, state : SessionState?, pid : Int?) : List<Session>
}