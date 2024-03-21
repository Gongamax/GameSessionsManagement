package pt.isel.ls.sessions.http.model.game

import kotlinx.serialization.Serializable

@Serializable
data class GameDTO(
    val name: String,
    val developer: String,
    val genres: List<String>,
)

@Serializable
data class GamesInputModel(
    val developer: String,
    val genres: List<String>,
)
