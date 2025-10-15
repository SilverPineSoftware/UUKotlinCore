package com.silverpine.uu.core.serialization

import com.silverpine.uu.core.serialization.UUEnumFormat.Name
import com.silverpine.uu.core.serialization.UUEnumFormat.NameLower
import com.silverpine.uu.core.serialization.UUEnumFormat.NameSnakeCase
import com.silverpine.uu.core.serialization.UUEnumFormat.Ordinal
import kotlinx.serialization.descriptors.PrimitiveKind

/**
 * Defines the supported formats for serializing and deserializing enum values in [UUEnumSerializer] and [UUSafeEnumSerializer].
 *
 * Each format controls how an enum constant is represented in JSON, and determines whether it is encoded as a string or integer.
 * This abstraction allows flexible compatibility with external APIs, legacy systems, and naming conventions.
 *
 * ### Format Options
 * - [Name] — Uses the exact enum name as-is (e.g. `"RED_BLUE"`).
 * - [NameLower] — Uses the lowercase version of the enum name (e.g. `"red_blue"`).
 * - [NameSnakeCase] — Converts the enum name to snake_case using `uuToSnakeCase()` (e.g. `"red_blue"`).
 * - [Ordinal] — Uses the ordinal index of the enum constant (e.g. `0`, `1`, `2`).
 *
 * ### Serialization Type
 * Each format maps to a [PrimitiveKind] used by KotlinX Serialization:
 * - `STRING` for [Name], [NameLower], and [NameSnakeCase]
 * - `INT` for [Ordinal]
 *
 * @property primitiveKind The KotlinX [PrimitiveKind] associated with this format.
 */
enum class UUEnumFormat
{
    Name,
    NameLower,
    NameSnakeCase,
    Ordinal;

    /**
     * The [PrimitiveKind] used for serialization based on the format.
     * - `STRING` for name-based formats
     * - `INT` for ordinal format
     */
    val primitiveKind: PrimitiveKind
        get()
        {
            return when (this)
            {
                Name, NameLower, NameSnakeCase -> PrimitiveKind.STRING
                Ordinal -> PrimitiveKind.INT
            }
        }

    companion object
    {
        /**
         * The default format used when none is explicitly specified.
         * Defaults to [Name].
         */
        val Default = Name
    }
}