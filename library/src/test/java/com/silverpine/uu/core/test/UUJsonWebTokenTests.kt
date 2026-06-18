package com.silverpine.uu.core.test

import com.silverpine.uu.core.UUEncryptedJsonWebToken
import com.silverpine.uu.core.UUJsonWebToken
import com.silverpine.uu.core.UUJwtConstants
import com.silverpine.uu.core.UUJwtException
import com.silverpine.uu.core.UUSignedJsonWebToken
import com.silverpine.uu.core.keyID
import com.silverpine.uu.core.uuJwtKeyID
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.Base64
import java.util.Date

// MARK: - Test vectors

private object JwtTestVectors
{
    // jwt.io HS256 example (signature not verified here).
    const val signedToken =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
            "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ." +
            "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"

    val encryptedToken: String by lazy {
        val header = base64URL(
            json = mapOf(
                UUJwtConstants.Header.ALGORITHM to "RSA-OAEP",
                UUJwtConstants.Header.ENCRYPTION to "A256GCM",
            ),
        )
        val encryptedKey = base64URL(byteArrayOf(0x01, 0x02, 0x03))
        val iv = base64URL(byteArrayOf(0x0A, 0x0B, 0x0C))
        val ciphertext = base64URL("secret-payload".toByteArray(Charsets.UTF_8))
        val authTag = base64URL(byteArrayOf(0xAA.toByte(), 0xBB.toByte(), 0xCC.toByte()))
        listOf(header, encryptedKey, iv, ciphertext, authTag).joinToString(".")
    }

    fun base64URL(json: Map<String, Any>): String =
        base64URL(
            buildJsonObject {
                json.forEach { (key, value) ->
                    when (value)
                    {
                        is String -> put(key, JsonPrimitive(value))
                        is Number -> put(key, JsonPrimitive(value.toLong()))
                        else -> put(key, JsonPrimitive(value.toString()))
                    }
                }
            }.toString().toByteArray(Charsets.UTF_8),
        )

    fun base64URL(data: ByteArray): String =
        Base64.getEncoder()
            .encodeToString(data)
            .replace('+', '-')
            .replace('/', '_')
            .replace("=", "")

    fun signedToken(
        header: Map<String, Any>,
        payload: Map<String, Any>,
        signature: ByteArray = byteArrayOf(0xDE.toByte(), 0xAD.toByte(), 0xBE.toByte(), 0xEF.toByte()),
    ): String =
        listOf(
            base64URL(header),
            base64URL(payload),
            base64URL(signature),
        ).joinToString(".")
}

// MARK: - Assertions

private fun <T> assertParseFailure(
    result: Result<T>,
    expected: UUJwtException,
)
{
    assertTrue(result.isFailure, "Expected failure $expected, got success")
    assertEquals(expected, result.exceptionOrNull())
}

// MARK: - Parse errors

class UUJwtExceptionTests
{
    @Test
    fun message_isNonEmptyForAllCases()
    {
        val exceptions = listOf(
            UUJwtException.InvalidPartCount(2),
            UUJwtException.EmptyPart(1),
            UUJwtException.InvalidBase64(0),
            UUJwtException.InvalidJson(1),
            UUJwtException.MissingRequiredHeaderField(UUJwtConstants.Header.ALGORITHM),
        )

        for (exception in exceptions)
        {
            assertFalse(exception.message.isNullOrEmpty(), "Missing message for $exception")
        }
    }
}

// MARK: - Signed token

class UUSignedJsonWebTokenTests
{
    @Test
    fun parse_decodesKnownToken()
    {
        val token = UUSignedJsonWebToken.parse(JwtTestVectors.signedToken).getOrThrow()
        assertEquals(JwtTestVectors.signedToken, token.compactSerialization)
        assertEquals("HS256", token.algorithm)
        assertEquals("JWT", token.type)
        assertEquals("1234567890", token.subject)
        assertEquals("John Doe", token.payload["name"])
        assertEquals(Date(1_516_239_022L * 1000L), token.issuedAt)
        assertTrue(token.signature.isNotEmpty())
    }

    @Test
    fun parse_get_returnsSuccessValue()
    {
        val token = UUSignedJsonWebToken.parse(JwtTestVectors.signedToken).getOrThrow()
        assertEquals("HS256", token.algorithm)
    }

    @Test
    fun parse_get_rethrowsFailure()
    {
        assertParseFailure(
            UUSignedJsonWebToken.parse("only.two"),
            UUJwtException.InvalidPartCount(2),
        )
    }

    @Test
    fun parse_trimsWhitespace()
    {
        val token = UUSignedJsonWebToken.parse("  ${JwtTestVectors.signedToken} \n").getOrThrow()
        assertEquals(JwtTestVectors.signedToken, token.compactSerialization)
    }

    @Test
    fun parse_returnsFailureForBearerPrefix()
    {
        assertParseFailure(
            UUSignedJsonWebToken.parse("Bearer ${JwtTestVectors.signedToken}"),
            UUJwtException.InvalidBase64(0),
        )
    }

    @Test
    fun parse_returnsFailureForInvalidPartCount()
    {
        assertParseFailure(
            UUSignedJsonWebToken.parse("only.two"),
            UUJwtException.InvalidPartCount(2),
        )
        assertParseFailure(
            UUSignedJsonWebToken.parse(JwtTestVectors.encryptedToken),
            UUJwtException.InvalidPartCount(5),
        )
    }

    @Test
    fun parse_returnsFailureForEmptyPart()
    {
        assertParseFailure(UUSignedJsonWebToken.parse(".a.b"), UUJwtException.EmptyPart(0))
        assertParseFailure(UUSignedJsonWebToken.parse("a..c"), UUJwtException.EmptyPart(1))
        assertParseFailure(UUSignedJsonWebToken.parse(".a.b.c"), UUJwtException.InvalidPartCount(4))
    }

    @Test
    fun parse_returnsFailureForInvalidBase64()
    {
        val token = "%%%." +
            JwtTestVectors.base64URL(mapOf(UUJwtConstants.Header.ALGORITHM to "HS256")) +
            ".signature"

        assertParseFailure(UUSignedJsonWebToken.parse(token), UUJwtException.InvalidBase64(0))
    }

    @Test
    fun parse_returnsFailureForInvalidHeaderJson()
    {
        val header = JwtTestVectors.base64URL("not-json".toByteArray(Charsets.UTF_8))
        val payload = JwtTestVectors.base64URL(mapOf(UUJwtConstants.Claim.SUBJECT to "1"))
        val signature = JwtTestVectors.base64URL(byteArrayOf(0x01))

        assertParseFailure(
            UUSignedJsonWebToken.parse("$header.$payload.$signature"),
            UUJwtException.InvalidJson(0),
        )
    }

    @Test
    fun parse_returnsFailureForInvalidPayloadJson()
    {
        val header = JwtTestVectors.base64URL(mapOf(UUJwtConstants.Header.ALGORITHM to "HS256"))
        val payload = JwtTestVectors.base64URL("[1,2,3]".toByteArray(Charsets.UTF_8))
        val signature = JwtTestVectors.base64URL(byteArrayOf(0x01))

        assertParseFailure(
            UUSignedJsonWebToken.parse("$header.$payload.$signature"),
            UUJwtException.InvalidJson(1),
        )
    }

    @Test
    fun parse_returnsFailureWhenAlgHeaderMissing()
    {
        val token = JwtTestVectors.signedToken(
            header = mapOf(UUJwtConstants.Header.TYPE to "JWT"),
            payload = mapOf(UUJwtConstants.Claim.SUBJECT to "1"),
        )

        assertParseFailure(
            UUSignedJsonWebToken.parse(token),
            UUJwtException.MissingRequiredHeaderField(UUJwtConstants.Header.ALGORITHM),
        )
    }

    @Test
    fun parse_returnsFailureWhenAlgHeaderEmpty()
    {
        val token = JwtTestVectors.signedToken(
            header = mapOf(UUJwtConstants.Header.ALGORITHM to ""),
            payload = mapOf(UUJwtConstants.Claim.SUBJECT to "1"),
        )

        assertParseFailure(
            UUSignedJsonWebToken.parse(token),
            UUJwtException.MissingRequiredHeaderField(UUJwtConstants.Header.ALGORITHM),
        )
    }

    @Test
    fun claimAccessors_returnNilWhenAbsent()
    {
        val token = UUSignedJsonWebToken.parse(
            JwtTestVectors.signedToken(
                header = mapOf(UUJwtConstants.Header.ALGORITHM to "none"),
                payload = emptyMap(),
            ),
        ).getOrThrow()

        assertEquals("none", token.algorithm)
        assertNull(token.type)
        assertNull(token.subject)
        assertNull(token.issuer)
        assertNull(token.audience)
        assertNull(token.expiration)
        assertNull(token.issuedAt)
        assertNull(token.notBefore)
        assertNull(token.keyID)
    }

    @Test
    fun keyID_returnsValueFromHeader()
    {
        val token = UUSignedJsonWebToken.parse(
            JwtTestVectors.signedToken(
                header = mapOf(
                    UUJwtConstants.Header.ALGORITHM to "HS256",
                    UUJwtConstants.Header.KEY_ID to "my-key-id",
                ),
                payload = emptyMap(),
            ),
        ).getOrThrow()

        assertEquals("my-key-id", token.keyID)
        assertEquals("my-key-id", token.header.uuJwtKeyID())
    }

    @Test
    fun uuJwtKeyID_returnsNullForNonStringValue()
    {
        val header = mapOf<String, Any?>(UUJwtConstants.Header.KEY_ID to 123)
        assertNull(header.uuJwtKeyID())
    }

    @Test
    fun claimAccessors_parseNumericDatesFromNSNumber()
    {
        val token = UUSignedJsonWebToken.parse(
            JwtTestVectors.signedToken(
                header = mapOf(UUJwtConstants.Header.ALGORITHM to "HS256"),
                payload = mapOf(
                    UUJwtConstants.Claim.EXPIRATION to 1_700_000_000L,
                    UUJwtConstants.Claim.NOT_BEFORE to 1_600_000_000L,
                ),
            ),
        ).getOrThrow()

        assertEquals(Date(1_700_000_000L * 1000L), token.expiration)
        assertEquals(Date(1_600_000_000L * 1000L), token.notBefore)
    }
}

// MARK: - Encrypted token

class UUEncryptedJsonWebTokenTests
{
    @Test
    fun parse_decodesFivePartToken()
    {
        val token = UUEncryptedJsonWebToken.parse(JwtTestVectors.encryptedToken).getOrThrow()
        assertEquals(JwtTestVectors.encryptedToken, token.compactSerialization)
        assertEquals("RSA-OAEP", token.algorithm)
        assertEquals("A256GCM", token.encryption)
        assertTrue(token.encryptedKey.contentEquals(byteArrayOf(0x01, 0x02, 0x03)))
        assertTrue(token.iv.contentEquals(byteArrayOf(0x0A, 0x0B, 0x0C)))
        assertTrue(token.ciphertext.contentEquals("secret-payload".toByteArray(Charsets.UTF_8)))
        assertTrue(token.authTag.contentEquals(byteArrayOf(0xAA.toByte(), 0xBB.toByte(), 0xCC.toByte())))
    }

    @Test
    fun parse_trimsWhitespace()
    {
        val token = UUEncryptedJsonWebToken.parse("  ${JwtTestVectors.encryptedToken}  ").getOrThrow()
        assertEquals(JwtTestVectors.encryptedToken, token.compactSerialization)
    }

    @Test
    fun keyID_returnsValueFromProtectedHeader()
    {
        val header = JwtTestVectors.base64URL(
            mapOf(
                UUJwtConstants.Header.ALGORITHM to "RSA-OAEP",
                UUJwtConstants.Header.ENCRYPTION to "A256GCM",
                UUJwtConstants.Header.KEY_ID to "jwe-key-id",
            ),
        )
        val tokenString = listOf(
            header,
            JwtTestVectors.base64URL(byteArrayOf(0x01)),
            JwtTestVectors.base64URL(byteArrayOf(0x02)),
            JwtTestVectors.base64URL(byteArrayOf(0x03)),
            JwtTestVectors.base64URL(byteArrayOf(0x04)),
        ).joinToString(".")

        val token = UUEncryptedJsonWebToken.parse(tokenString).getOrThrow()
        assertEquals("jwe-key-id", token.keyID)
        assertEquals("jwe-key-id", token.protectedHeader.uuJwtKeyID())
    }

    @Test
    fun parse_returnsFailureForInvalidPartCount()
    {
        assertParseFailure(
            UUEncryptedJsonWebToken.parse(JwtTestVectors.signedToken),
            UUJwtException.InvalidPartCount(3),
        )
    }

    @Test
    fun parse_returnsFailureWhenAlgMissing()
    {
        val header = JwtTestVectors.base64URL(mapOf(UUJwtConstants.Header.ENCRYPTION to "A256GCM"))
        val token = listOf(
            header,
            JwtTestVectors.base64URL(byteArrayOf(0x01)),
            JwtTestVectors.base64URL(byteArrayOf(0x02)),
            JwtTestVectors.base64URL(byteArrayOf(0x03)),
            JwtTestVectors.base64URL(byteArrayOf(0x04)),
        ).joinToString(".")

        assertParseFailure(
            UUEncryptedJsonWebToken.parse(token),
            UUJwtException.MissingRequiredHeaderField(UUJwtConstants.Header.ALGORITHM),
        )
    }

    @Test
    fun parse_returnsFailureWhenEncMissing()
    {
        val header = JwtTestVectors.base64URL(mapOf(UUJwtConstants.Header.ALGORITHM to "RSA-OAEP"))
        val token = listOf(
            header,
            JwtTestVectors.base64URL(byteArrayOf(0x01)),
            JwtTestVectors.base64URL(byteArrayOf(0x02)),
            JwtTestVectors.base64URL(byteArrayOf(0x03)),
            JwtTestVectors.base64URL(byteArrayOf(0x04)),
        ).joinToString(".")

        assertParseFailure(
            UUEncryptedJsonWebToken.parse(token),
            UUJwtException.MissingRequiredHeaderField(UUJwtConstants.Header.ENCRYPTION),
        )
    }

    @Test
    fun parse_allowsEmptyEncryptedKeySegment()
    {
        val header = JwtTestVectors.base64URL(
            mapOf(
                UUJwtConstants.Header.ALGORITHM to "dir",
                UUJwtConstants.Header.ENCRYPTION to "A256GCM",
            ),
        )
        val token = listOf(
            header,
            "",
            JwtTestVectors.base64URL(byteArrayOf(0x0A)),
            JwtTestVectors.base64URL(byteArrayOf(0x0B)),
            JwtTestVectors.base64URL(byteArrayOf(0x0C)),
        ).joinToString(".")

        val parsed = UUEncryptedJsonWebToken.parse(token).getOrThrow()
        assertTrue(parsed.encryptedKey.isEmpty())
    }
}

// MARK: - Entry point

class UUJsonWebTokenTests
{
    @Test
    fun parse_returnsSignedForThreeParts()
    {
        val token = UUJsonWebToken.parse(JwtTestVectors.signedToken).getOrThrow()
        assertTrue(token is UUJsonWebToken.Signed)
        assertEquals("HS256", (token as UUJsonWebToken.Signed).token.algorithm)
    }

    @Test
    fun parse_returnsEncryptedForFiveParts()
    {
        val token = UUJsonWebToken.parse(JwtTestVectors.encryptedToken).getOrThrow()
        assertTrue(token is UUJsonWebToken.Encrypted)
        assertEquals("A256GCM", (token as UUJsonWebToken.Encrypted).token.encryption)
    }

    @Test
    fun parse_returnsFailureForUnsupportedPartCount()
    {
        assertParseFailure(UUJsonWebToken.parse("one"), UUJwtException.InvalidPartCount(1))
        assertParseFailure(UUJsonWebToken.parse("a.b.c.d"), UUJwtException.InvalidPartCount(4))
    }
}
