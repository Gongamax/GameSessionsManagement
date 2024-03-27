package pt.isel.ls.sessions.repository

import kotlinx.datetime.LocalDateTime
import pt.isel.ls.sessions.domain.player.Player
import pt.isel.ls.sessions.domain.session.Session
import pt.isel.ls.sessions.domain.session.SessionState

interface SessionRepository {
    fun createSession(
        capacity: Int,
        gid: UInt,
        date: LocalDateTime,
    ): UInt

    fun addPlayerToSession(
        sid: UInt,
        player: Player,
    )

    fun getSession(sid: UInt): Session?

    fun getSessions(
        gid: UInt,
        date: LocalDateTime?,
        state: SessionState?,
        pid: UInt?,
        limit: Int,
        skip: Int,
    ): List<Session>

    fun reset()
}
