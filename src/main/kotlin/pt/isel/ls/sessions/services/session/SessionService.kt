package pt.isel.ls.sessions.services.session

import kotlinx.datetime.LocalDateTime
import pt.isel.ls.sessions.domain.session.Session
import pt.isel.ls.sessions.domain.session.SessionState
import pt.isel.ls.sessions.repository.SessionRepository
import pt.isel.ls.sessions.services.game.GameService
import pt.isel.ls.sessions.services.player.PlayerService
import pt.isel.ls.utils.Either
import pt.isel.ls.utils.failure
import pt.isel.ls.utils.success

//TODO: USE EITHER CODING STYLE
class SessionService(
    private val sessionRepository: SessionRepository,
    private val playerService: PlayerService,
    private val gameService: GameService
) {
    fun createSession(capacity: Int, gid: Int, date: LocalDateTime): Int {
        if (capacity <= 0) throw IllegalArgumentException("Capacity must be greater than 0")
        if (gameService.getGame(gid) == null) throw IllegalArgumentException("Game not found")
//        if (date < LocalDateTime.now()) throw IllegalArgumentException("Date must be in the future")
        return sessionRepository.createSession(capacity, gid, date)
    }

    fun addPlayerToSession(sid: Int, pid: Int) {
        val session = sessionRepository.getSession(sid) ?: throw IllegalArgumentException("Session not found")
        val playerToAdd = playerService.getDetailsPlayer(pid)
        if (session.associatedPlayers.size >= session.capacity) {
            throw IllegalArgumentException("Session is full")
        }
        if (playerToAdd == null) {
            throw IllegalArgumentException("Player not found")
        }
        if (session.associatedPlayers.contains(playerToAdd)) {
            throw IllegalArgumentException("Player is already in the session")
        }
        sessionRepository.addPlayerToSession(sid, playerToAdd)
    }

    fun getSession(sid: Int): Session {
        return sessionRepository.getSession(sid) ?: throw IllegalArgumentException("Session not found")
    }

    //TODO: SARAIVA
    fun getSessions(
        gid: Int,
        date: LocalDateTime? = null,
        state: SessionState? = null,
        pid: Int? = null
    ): SessionsGetResult = run{
        val game = gameService.getGame(gid)
        when(game){
            is Either.Left -> failure(SessionsGetError.GameNotFound)
            is Either.Right -> {
                val sessions = sessionRepository.getSessions(gid, date, state, pid)
                if (sessions.isNotEmpty()) success(sessions)
                else failure(SessionsGetError.SessionNotFound)
            }
        }

    }

}