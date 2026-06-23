package com.silverpine.uu.core

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.longOrNull
import java.util.Base64
import java.util.Date

/**
 * Standard JSON Web Token field names used by [UUJsonWebToken] and related APIs.
 *
 * @since 1.0.0
 */
object UUJwtConstants
{
    /**
     * JOSE header parameter names (RFC 7515 / RFC 7516).
     */
    object Header
    {
        /** `alg` — cryptographic algorithm or key management algorithm. */
        const val ALGORITHM = "alg"

        /** `typ` — media type (often `JWT`). */
        const val TYPE = "typ"

        /** `enc` — content encryption algorithm (JWE). */
        const val ENCRYPTION = "enc"

        /** `kid` — key identifier. */
        const val KEY_ID = "kid"
    }

    /**
     * Registered JWT claims (RFC 7519).
     */
    object Claim
    {
        /** `sub` — subject. */
        const val SUBJECT = "sub"

        /** `iss` — issuer. */
        const val ISSUER = "iss"

        /** `aud` — audience. */
        const val AUDIENCE = "aud"

        /** `exp` — expiration time. */
        const val EXPIRATION = "exp"

        /** `iat` — issued at. */
        const val ISSUED_AT = "iat"

        /** `nbf` — not before. */
        const val NOT_BEFORE = "nbf"
    }
}

/**
 * Exceptions produced by JWT operations in UUKotlinCore.
 *
 * @since 1.0.0
 */
sealed class UUJwtException(message: String) : Exception(message)
{
    /** The token does not contain exactly three (JWS) or five (JWE) segments. */
    data class InvalidPartCount(val count: Int) : UUJwtException(
        "JWT compact serialization requires 3 (JWS) or 5 (JWE) parts; found $count.",
    )

    /** A segment between dots was empty. */
    data class EmptyPart(val index: Int) : UUJwtException(
        "JWT part $index is empty.",
    )

    /** A segment was not valid Base64URL. */
    data class InvalidBase64(val part: Int) : UUJwtException(
        "JWT part $part is not valid Base64URL.",
    )

    /** A segment decoded successfully but was not a JSON object. */
    data class InvalidJson(val part: Int) : UUJwtException(
        "JWT part $part is not valid JSON object.",
    )

    /** A required JOSE header field was absent or not a non-empty string. */
    data class MissingRequiredHeaderField(val field: String) : UUJwtException(
        "JWT header is missing required field '$field'.",
    )
}

/**
 * A compact JSON Web Token, either signed (JWS) or encrypted (JWE).
 *
 * @since 1.0.0
 */
sealed class UUJsonWebToken
{
    /** A signed token (JWS) with three Base64URL-encoded parts. */
    data class Signed(val token: UUSignedJsonWebToken) : UUJsonWebToken()

    /** An encrypted token (JWE) with five Base64URL-encoded parts. */
    data class Encrypted(val token: UUEncryptedJsonWebToken) : UUJsonWebToken()

    companion object
    {
        /**
         * Parses a compact JWT string into a signed or encrypted token.
         *
         * Leading and trailing whitespace is trimmed.
         */
        fun parse(string: String): Result<UUJsonWebToken> = runCatching { parseOrThrow(string) }

        internal fun parseOrThrow(string: String): UUJsonWebToken
        {
            val (compactSerialization, parts) = UUJwtParser.partsFrom(string)

            return when (parts.size)
            {
                3 -> Signed(UUSignedJsonWebToken.parseOrThrow(parts, compactSerialization))

                5 -> Encrypted(UUEncryptedJsonWebToken.parseOrThrow(parts, compactSerialization))

                else -> throw UUJwtException.InvalidPartCount(parts.size)
            }
        }
    }
}

/**
 * A signed JSON Web Token (JWS) parsed from compact serialization.
 *
 * @since 1.0.0
 */
data class UUSignedJsonWebToken(
    val compactSerialization: String,
    val header: Map<String, Any?>,
    val payload: Map<String, Any?>,
    val signature: ByteArray,
)
{
    /** `alg` header value when present. */
    val algorithm: String?
        get() = header[UUJwtConstants.Header.ALGORITHM] as? String

    /** `typ` header value when present. */
    val type: String?
        get() = header[UUJwtConstants.Header.TYPE] as? String

    /** `sub` claim when present. */
    val subject: String?
        get() = payload[UUJwtConstants.Claim.SUBJECT] as? String

    /** `iss` claim when present. */
    val issuer: String?
        get() = payload[UUJwtConstants.Claim.ISSUER] as? String

    /** `aud` claim when present (string audience only). */
    val audience: String?
        get() = payload[UUJwtConstants.Claim.AUDIENCE] as? String

    /** `exp` claim as a [Date] when present. */
    val expiration: Date?
        get() = UUJwtParser.dateFromNumericDateClaim(payload[UUJwtConstants.Claim.EXPIRATION])

    /** `iat` claim as a [Date] when present. */
    val issuedAt: Date?
        get() = UUJwtParser.dateFromNumericDateClaim(payload[UUJwtConstants.Claim.ISSUED_AT])

    /** `nbf` claim as a [Date] when present. */
    val notBefore: Date?
        get() = UUJwtParser.dateFromNumericDateClaim(payload[UUJwtConstants.Claim.NOT_BEFORE])

    override fun equals(other: Any?): Boolean
    {
        if (this === other) return true
        if (other !is UUSignedJsonWebToken) return false
        return compactSerialization == other.compactSerialization &&
            header == other.header &&
            payload == other.payload &&
            signature.contentEquals(other.signature)
    }

    override fun hashCode(): Int
    {
        var result = compactSerialization.hashCode()
        result = 31 * result + header.hashCode()
        result = 31 * result + payload.hashCode()
        result = 31 * result + signature.contentHashCode()
        return result
    }

    override fun toString(): String
    {
        return "raw: $compactSerialization\nheader: $header\npayload: $payload\nsignature: ${signature.uuBase64()}"
    }

    companion object
    {
        /**
         * Parses a compact JWS string.
         */
        fun parse(string: String): Result<UUSignedJsonWebToken> = runCatching { parseOrThrow(string) }

        internal fun parseOrThrow(string: String): UUSignedJsonWebToken
        {
            val (compactSerialization, parts) = UUJwtParser.partsFrom(string)

            if (parts.size != 3)
            {
                throw UUJwtException.InvalidPartCount(parts.size)
            }

            return parseOrThrow(parts, compactSerialization)
        }

        internal fun parseOrThrow(
            parts: List<String>,
            compactSerialization: String,
        ): UUSignedJsonWebToken
        {
            UUJwtParser.requireNonEmptyParts(parts)

            val headerData = UUJwtParser.decodeBase64Part(parts[0], part = 0)
            val header = UUJwtParser.decodeJsonObject(headerData, part = 0)
            UUJwtParser.requiredHeaderString(header, UUJwtConstants.Header.ALGORITHM)

            val payloadData = UUJwtParser.decodeBase64Part(parts[1], part = 1)
            val payload = UUJwtParser.decodeJsonObject(payloadData, part = 1)

            val signature = UUJwtParser.decodeBase64Part(parts[2], part = 2)

            return UUSignedJsonWebToken(
                compactSerialization = compactSerialization,
                header = header,
                payload = payload,
                signature = signature,
            )
        }
    }
}

/**
 * An encrypted JSON Web Token (JWE) parsed from compact serialization.
 *
 * @since 1.0.0
 */
data class UUEncryptedJsonWebToken(
    val compactSerialization: String,
    val protectedHeader: Map<String, Any?>,
    val encryptedKey: ByteArray,
    val iv: ByteArray,
    val ciphertext: ByteArray,
    val authTag: ByteArray,
)
{
    /** `alg` protected-header value when present. */
    val algorithm: String?
        get() = protectedHeader[UUJwtConstants.Header.ALGORITHM] as? String

    /** `enc` protected-header value when present. */
    val encryption: String?
        get() = protectedHeader[UUJwtConstants.Header.ENCRYPTION] as? String

    override fun equals(other: Any?): Boolean
    {
        if (this === other) return true
        if (other !is UUEncryptedJsonWebToken) return false
        return compactSerialization == other.compactSerialization &&
            protectedHeader == other.protectedHeader &&
            encryptedKey.contentEquals(other.encryptedKey) &&
            iv.contentEquals(other.iv) &&
            ciphertext.contentEquals(other.ciphertext) &&
            authTag.contentEquals(other.authTag)
    }

    override fun hashCode(): Int
    {
        var result = compactSerialization.hashCode()
        result = 31 * result + protectedHeader.hashCode()
        result = 31 * result + encryptedKey.contentHashCode()
        result = 31 * result + iv.contentHashCode()
        result = 31 * result + ciphertext.contentHashCode()
        result = 31 * result + authTag.contentHashCode()
        return result
    }

    override fun toString(): String
    {
        return "raw: $compactSerialization\nprotectedHeader: $protectedHeader\nencryptedKey: ${encryptedKey.uuBase64()}\niv: ${iv.uuBase64()}\nciphertext: ${ciphertext.uuBase64()}\nauthTag: ${authTag.uuBase64()}"
    }

    companion object
    {
        /**
         * Parses a compact JWE string.
         */
        fun parse(string: String): Result<UUEncryptedJsonWebToken> = runCatching { parseOrThrow(string) }

        internal fun parseOrThrow(string: String): UUEncryptedJsonWebToken
        {
            val (compactSerialization, parts) = UUJwtParser.partsFrom(string)

            if (parts.size != 5)
            {
                throw UUJwtException.InvalidPartCount(parts.size)
            }

            return parseOrThrow(parts, compactSerialization)
        }

        internal fun parseOrThrow(
            parts: List<String>,
            compactSerialization: String,
        ): UUEncryptedJsonWebToken
        {
            // The encrypted-key segment may be empty for direct key agreement (`dir`).
            UUJwtParser.requireNonEmptyParts(parts, except = setOf(1))

            val headerData = UUJwtParser.decodeBase64Part(parts[0], part = 0)
            val protectedHeader = UUJwtParser.decodeJsonObject(headerData, part = 0)
            UUJwtParser.requiredHeaderString(protectedHeader, UUJwtConstants.Header.ALGORITHM)
            UUJwtParser.requiredHeaderString(protectedHeader, UUJwtConstants.Header.ENCRYPTION)

            val encryptedKey = UUJwtParser.decodeBase64Part(parts[1], part = 1, allowsEmpty = true)
            val iv = UUJwtParser.decodeBase64Part(parts[2], part = 2)
            val ciphertext = UUJwtParser.decodeBase64Part(parts[3], part = 3)
            val authTag = UUJwtParser.decodeBase64Part(parts[4], part = 4)

            return UUEncryptedJsonWebToken(
                compactSerialization = compactSerialization,
                protectedHeader = protectedHeader,
                encryptedKey = encryptedKey,
                iv = iv,
                ciphertext = ciphertext,
                authTag = authTag,
            )
        }
    }
}

private object UUJwtParser
{
    private val jwtJson = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    fun partsFrom(string: String): Pair<String, List<String>>
    {
        val compactSerialization = normalizedToken(string)
        val parts = compactSerialization.split('.')
        return compactSerialization to parts
    }

    fun requireNonEmptyParts(
        parts: List<String>,
        except: Set<Int> = emptySet(),
    )
    {
        parts.forEachIndexed { index, part ->
            if (part.isEmpty() && index !in except)
            {
                throw UUJwtException.EmptyPart(index)
            }
        }
    }

    fun normalizedToken(string: String): String = string.trim()

    fun decodeBase64Part(
        segment: String,
        part: Int,
        allowsEmpty: Boolean = false,
    ): ByteArray
    {
        if (segment.isEmpty())
        {
            if (!allowsEmpty)
            {
                throw UUJwtException.EmptyPart(part)
            }

            return ByteArray(0)
        }

        return segment.uuJwtBase64UrlDecode()
            ?: throw UUJwtException.InvalidBase64(part)
    }

    fun decodeJsonObject(
        data: ByteArray,
        part: Int,
    ): Map<String, Any?>
    {
        return try
        {
            when (val element = jwtJson.parseToJsonElement(String(data, Charsets.UTF_8)))
            {
                is JsonObject -> element.toJwtMap()

                else -> throw UUJwtException.InvalidJson(part)
            }
        }
        catch (ex: UUJwtException)
        {
            throw ex
        }
        catch (_: Exception)
        {
            throw UUJwtException.InvalidJson(part)
        }
    }

    fun requiredHeaderString(
        header: Map<String, Any?>,
        key: String,
    ): String
    {
        val value = header[key] as? String

        if (value.isNullOrEmpty())
        {
            throw UUJwtException.MissingRequiredHeaderField(key)
        }

        return value
    }

    fun dateFromNumericDateClaim(value: Any?): Date?
    {
        val seconds = when (value)
        {
            is Number -> value.toDouble()
            else -> null
        } ?: return null

        return Date((seconds * 1000.0).toLong())
    }

    private fun JsonObject.toJwtMap(): Map<String, Any?> =
        mapValues { (_, value) -> value.toJwtValue() }

    private fun JsonElement.toJwtValue(): Any? = when (this)
    {
        is JsonNull -> null

        is JsonObject -> mapValues { (_, value) -> value.toJwtValue() }

        is JsonArray -> map { it.toJwtValue() }

        is JsonPrimitive -> when
        {
            isString -> content
            longOrNull != null -> longOrNull
            doubleOrNull != null -> doubleOrNull
            booleanOrNull != null -> booleanOrNull
            else -> content
        }
    }

    private fun String.uuJwtBase64UrlDecode(): ByteArray?
    {
        var tmp = replace('-', '+').replace('_', '/')
        val remainder = tmp.length % 4

        if (remainder != 0)
        {
            tmp = tmp.padEnd(tmp.length + (4 - remainder), '=')
        }

        return try
        {
            Base64.getDecoder().decode(tmp)
        }
        catch (_: IllegalArgumentException)
        {
            null
        }
    }
}

/**
 * Returns the `kid` (key ID) JOSE header value when present.
 *
 * @since 1.0.0
 */
fun Map<String, Any?>.uuJwtKeyID(): String? =
    this[UUJwtConstants.Header.KEY_ID] as? String

/** `kid` header value when present. */
val UUSignedJsonWebToken.keyID: String?
    get() = header.uuJwtKeyID()

/** `kid` protected-header value when present. */
val UUEncryptedJsonWebToken.keyID: String?
    get() = protectedHeader.uuJwtKeyID()
