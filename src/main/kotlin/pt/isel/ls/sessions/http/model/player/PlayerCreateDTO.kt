package pt.isel.ls.sessions.http.model.player

import kotlinx.serialization.Serializable

/**
 * Data class representing a PlayerDTO for creation purposes.
 *
 * @property name The name of the player.
 * @property email The email of the player.
 */
@Serializable
data class PlayerCreateDTO(val name: String, val email: String)
