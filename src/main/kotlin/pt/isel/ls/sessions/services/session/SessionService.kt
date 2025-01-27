package pt.isel.ls.sessions.services.session

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pt.isel.ls.sessions.domain.session.SessionState
import pt.isel.ls.sessions.repository.GameRepository
import pt.isel.ls.sessions.repository.PlayerRepository
import pt.isel.ls.sessions.repository.SessionRepository
import pt.isel.ls.utils.failure
import pt.isel.ls.utils.success

class SessionService(
    private val sessionRepository: SessionRepository,
    private val playerRepository: PlayerRepository,
    private val gameRepository: GameRepository,
    private val clock: Clock,
) {
    fun createSession(
        capacity: Int,
        gid: UInt,
        date: LocalDateTime,
    ): SessionCreationResult =
        run {
            if (capacity <= 0) {
                return@run failure(SessionCreationError.InvalidCapacity)
            }
            if (gameRepository.getGameById(gid) == null) {
                return@run failure(SessionCreationError.GameNotFound)
            }
            val now = clock.now().toLocalDateTime(TimeZone.currentSystemDefault())
            if (date < now) {
                return@run failure(SessionCreationError.InvalidDate)
            }
            val sid = sessionRepository.createSession(capacity, gid, date)
            success(sid)
        }

    fun addPlayerToSession(
        sid: UInt,
        pid: UInt,
    ): SessionAddPlayerResult =
        run {
            val session = sessionRepository.getSession(sid) ?: return@run failure(SessionAddPlayerError.SessionNotFound)
            val playerToAdd =
                playerRepository.getPlayerById(pid) ?: return@run failure(SessionAddPlayerError.PlayerNotFound)
            if (session.associatedPlayers.size >= session.capacity) {
                return@run failure(SessionAddPlayerError.SessionFull)
            }
            if (session.associatedPlayers.contains(playerToAdd)) {
                return@run failure(SessionAddPlayerError.PlayerAlreadyInSession)
            }
            success(sessionRepository.addPlayerToSession(sid, playerToAdd))
        }

    fun getSession(sid: UInt): SessionGetResult =
        run {
            val session = sessionRepository.getSession(sid)
            if (session == null) {
                failure(SessionGetError.SessionNotFound)
            } else {
                success(session)
            }
        }

    fun getSessions(
        gid: UInt?,
        date: LocalDateTime? = null,
        state: SessionState? = null,
        pid: UInt? = null,
        limit: Int,
        skip: Int,
    ): SessionsGetResult =
        run {
            if (gid != null) {
                gameRepository.getGameById(gid) ?: return@run failure(SessionsGetError.GameNotFound)
            }
            if (state != null && state !in SessionState.entries) {
                return@run failure(SessionsGetError.InvalidState)
            }
            if (pid != null && playerRepository.getPlayerById(pid) == null) {
                return@run failure(SessionsGetError.PlayerNotFound)
            }
            val sessions = sessionRepository.getSessions(gid, date, state, pid, limit, skip)
            success(sessions)
        }

    fun deleteSession(sid: UInt): SessionDeleteResult =
        run {
            sessionRepository.getSession(sid) ?: return@run failure(SessionDeleteError.SessionNotFound)
            sessionRepository.deleteSession(sid)
            success(Unit)
        }

    fun updateSession(
        sid: UInt,
        capacity: Int,
        date: LocalDateTime,
    ): SessionUpdateResult =
        run {
            sessionRepository.getSession(sid) ?: return@run failure(SessionUpdateError.SessionNotFound)
            val now = clock.now().toLocalDateTime(TimeZone.currentSystemDefault())
            if (capacity <= 0) {
                return@run failure(SessionUpdateError.InvalidCapacity)
            }
            if (date < now) {
                return@run failure(SessionUpdateError.InvalidDate)
            }
            success(sessionRepository.updateSession(sid, capacity, date))
        }

    fun removePlayerFromSession(
        sid: UInt,
        pid: UInt,
    ): SessionRemovePlayerResult =
        run {
            val session =
                sessionRepository.getSession(sid) ?: return@run failure(SessionRemovePlayerError.SessionNotFound)
            val playerToRemove =
                playerRepository.getPlayerById(pid) ?: return@run failure(SessionRemovePlayerError.PlayerNotFound)
            if (!session.associatedPlayers.contains(playerToRemove)) {
                return@run failure(SessionRemovePlayerError.PlayerNotInSession)
            }
            success(sessionRepository.removePlayerFromSession(sid, playerToRemove.pid))
        }
}
