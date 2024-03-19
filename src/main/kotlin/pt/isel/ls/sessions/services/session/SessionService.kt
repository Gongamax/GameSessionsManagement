package pt.isel.ls.sessions.services.session

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pt.isel.ls.sessions.domain.session.Session
import pt.isel.ls.sessions.domain.session.SessionState
import pt.isel.ls.sessions.repository.SessionRepository
import pt.isel.ls.sessions.services.game.GameService
import pt.isel.ls.sessions.services.player.PlayerService
import pt.isel.ls.utils.*

class SessionService(
    private val sessionRepository: SessionRepository,
    private val playerService: PlayerService,
    private val gameService: GameService
) {
    fun createSession(capacity: Int, gid: UInt, date: LocalDateTime): SessionCreationResult = run {
        if (capacity <= 0)
            return@run failure(SessionCreationError.InvalidCapacity)
        if (gameService.getGame(gid) is Failure)
            return@run failure(SessionCreationError.GameNotFound)
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        if (date < now)
            return@run failure(SessionCreationError.InvalidDate)
        val sid = sessionRepository.createSession(capacity, gid, date)
        success(sid)
    }

    fun addPlayerToSession(sid: UInt, pid: UInt): SessionAddPlayerResult = run {
        val session = sessionRepository.getSession(sid) ?: return@run failure(SessionAddPlayerError.SessionNotFound)
        val playerToAdd = playerService.getDetailsPlayer(pid)
        if (session.associatedPlayers.size >= session.capacity)
            return@run failure(SessionAddPlayerError.SessionFull)
        when(playerToAdd) {
            is Failure -> return@run failure(SessionAddPlayerError.PlayerNotFound)
            is Success -> {
                if (session.associatedPlayers.contains(playerToAdd.value))
                    return@run failure(SessionAddPlayerError.PlayerAlreadyInSession)
                success(sessionRepository.addPlayerToSession(sid, playerToAdd.value))
            }
        }
    }

    fun getSession(sid: UInt): SessionGetResult = run {
        val session = sessionRepository.getSession(sid)
        if (session == null) failure(SessionGetError.SessionNotFound)
        else success(session)
    }

    fun getSessions(
        gid: UInt,
        date: LocalDateTime? = null,
        state: SessionState? = null,
        pid: UInt? = null
    ): SessionsGetResult = run {
        val game = gameService.getGame(gid)
        when (game) {
            is Failure -> failure(SessionsGetError.GameNotFound)
            is Success -> {
                val sessions = sessionRepository.getSessions(gid, date, state, pid)
                if (sessions.isNotEmpty()) success(sessions)
                else failure(SessionsGetError.SessionNotFound)
            }
        }
    }
}