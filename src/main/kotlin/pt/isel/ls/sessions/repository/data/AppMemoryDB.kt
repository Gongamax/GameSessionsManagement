package pt.isel.ls.sessions.repository.data

import kotlinx.datetime.Clock
import pt.isel.ls.sessions.repository.data.game.GameMemoryDB
import pt.isel.ls.sessions.repository.data.player.PlayerMemoryDB
import pt.isel.ls.sessions.repository.data.session.SessionMemoryDB

/**
 * This class represents the in-memory database for the application.
 * It contains instances of PlayerMemoryDB, SessionMemoryDB, and GameMemoryDB,
 * which are used to store and manage data related to players, sessions, and games respectively.
 */
class AppMemoryDB {
    // Clock object used to get the current time
    private val clock = Clock.System

    // In-memory database for storing and managing player data
    val playerMemoryDB = PlayerMemoryDB()

    // In-memory database for storing and managing session data
    val sessionMemoryDB = SessionMemoryDB(playerMemoryDB, clock)

    // In-memory database for storing and managing game data
    val gameMemoryDB = GameMemoryDB()
}