package pt.isel.ls.sessions.domain.session

/**
 * Enum class representing the different states of a session.
 *
 * @property OPEN Represents a session that is currently active and ongoing.
 * @property CLOSED Represents a session that has ended or is not currently active.
 */
enum class SessionState {
    OPEN,
    CLOSED,
}
// TODO: THIS NEEDS TO BE OKAY WITH UPPER AND LOWER CASE WHEN COMPARING
