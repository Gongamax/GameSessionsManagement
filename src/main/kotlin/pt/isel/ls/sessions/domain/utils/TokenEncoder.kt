package pt.isel.ls.sessions.domain.utils


/**
 * Creates validation information for a given token.
 *
 * @param token The token to be validated.
 * @return The validation information for the token.
 */
interface TokenEncoder {
    fun createValidationInformation(token: String) : TokenValidationInfo
}