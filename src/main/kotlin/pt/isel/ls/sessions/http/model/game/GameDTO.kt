package pt.isel.ls.sessions.http.model.game

import kotlinx.serialization.Serializable

@Serializable
data class GameOutputModel(
    val name: String,
    val developer: String,
    val genres: List<String>
)

@Serializable
data class GameInputModel(
    val name: String,
    val developer: String,
    val genres: List<String>
)
