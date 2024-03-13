package pt.isel.ls.sessions.http.model.utils

import kotlinx.serialization.Serializable


/**
 * Data class representing a TokenDTO.
 *
 * @property pid The player id associated with the token.
 * @property token The token string.
 */
@Serializable
data class TokenDTO(val pid: UInt, val token: String)