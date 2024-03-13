package pt.isel.ls.sessions.repository.data.session

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pt.isel.ls.sessions.domain.player.Player
import pt.isel.ls.sessions.domain.session.Session
import pt.isel.ls.sessions.domain.session.SessionState
import pt.isel.ls.sessions.repository.SessionRepository
import pt.isel.ls.sessions.repository.data.player.PlayerMemoryDB
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class SessionMemoryDB(
    private val clock: Clock = Clock.System
) : SessionRepository {
    val sessions = ConcurrentHashMap<Int, Session>()
    private val nextSessionId = AtomicInteger(1)

    override fun createSession(capacity: Int, gid: Int, date: LocalDateTime): Int {
        val sid = nextSessionId.getAndIncrement()
        val session = Session(sid, 0, date, gid, emptySet(), capacity)
        sessions[sid] = session
        return sid
    }

    override fun addPlayerToSession(sid: Int, player: Player) {
        val session = sessions[sid] ?: throw IllegalArgumentException("Session not found")
        val newPlayers = session.associatedPlayers + player
        sessions[sid] = session.copy(associatedPlayers = newPlayers)
    }

    override fun getSession(sid: Int): Session? {
        return sessions[sid]
    }

    override fun getSessions(gid: Int, date: LocalDateTime?, state: SessionState?, pid: UInt?): List<Session> {
        return sessions.values.filter {
            (it.gid == gid) &&
                    (date == null || it.date == date) &&
                    (state == null || state == getSessionState(it.date, it.associatedPlayers, it.capacity)) &&
                    (pid == null || it.associatedPlayers.any { p -> p.pid == pid })
        }
    }

    private fun getSessionState(date: LocalDateTime, players: Set<Player>, capacity: Int): SessionState {
        val now = clock.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return when {
            players.size < capacity -> SessionState.OPEN
            date < now -> SessionState.OPEN
            else -> SessionState.CLOSED
        }
    }
}