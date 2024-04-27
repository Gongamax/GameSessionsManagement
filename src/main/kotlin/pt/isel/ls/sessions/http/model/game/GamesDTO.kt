package pt.isel.ls.sessions.http.model.game

import kotlinx.serialization.Serializable

@Serializable
data class GamesDTO(
    val games: List<GameDTO>,
)
