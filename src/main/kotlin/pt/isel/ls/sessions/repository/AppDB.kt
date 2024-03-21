package pt.isel.ls.sessions.repository

interface AppDB {
    val playerDB: PlayerRepository
    val sessionDB: SessionRepository
    val gameDB: GameRepository
}