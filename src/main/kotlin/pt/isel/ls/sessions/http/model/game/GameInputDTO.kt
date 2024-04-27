package pt.isel.ls.sessions.http.model.game

import kotlinx.serialization.Serializable

@Serializable
data class GameInputDTO(
    val name: String,
    val developer: String,
    val genres: List<String>,
)
