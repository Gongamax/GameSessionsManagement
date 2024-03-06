package pt.isel.ls.sessions.domain.utils

import java.security.MessageDigest
import java.util.*

/**
 * This class is responsible for encoding tokens.
 * It uses the SHA256 algorithm to encode the token.
 */
class Sha256TokenEncoder : TokenEncoder {
    /**
     * Creates validation information for a given token.
     *
     * @param token The token to be validated.
     * @return The validation information for the token.
     */

    override fun createValidationInformation(token: String): TokenValidationInfo =
        TokenValidationInfo(hash(token))

    /**
     * Hashes the input string using the SHA256 algorithm.
     *
     * @param input The string to be hashed.
     * @return The hashed string.
     */
    private fun hash(input: String): String {
        val messageDigest = MessageDigest.getInstance("SHA256")
        return Base64.getUrlEncoder().encodeToString(
            messageDigest.digest(
                Charsets.UTF_8.encode(input).array()
            )
        )
    }
}