package pt.isel.ls.sessions.repository

import pt.isel.ls.sessions.domain.session.Session
import pt.isel.ls.sessions.domain.session.SessionState
import java.util.Date

interface SessionRepository {

    fun createSession(capacity : Int, gid : Int, date : Date) : Int

    fun addPlayerToSession(sid : Int, pid : Int)

    fun getSession(sid : Int) : Session

    fun getSessions(gid : Int, date : Date, state : SessionState, pid : Int) : List<Session>
}