package pt.isel.ls.sessions.domain.utils

import java.util.UUID


/**
 * Data class representing a Token.
 *
 * @property pid The unique identifier of the player.
 * @property token The unique token associated with the player.
 */
data class Token(val pid: UInt, val token: UUID)

