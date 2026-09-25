package com.silverpine.uu.core

/**
 * An enum with an explicit backing value independent of its ordinal or name.
 *
 * @param V The backing value type, such as UInt, Long, or String.
 * @property value The value used for serialization. Use unique values for one-to-one round trips;
 * aliases require the deserialization converter to choose a canonical enum constant.
 */
interface UUValueBackedEnum<V>
{
    val value: V
}
