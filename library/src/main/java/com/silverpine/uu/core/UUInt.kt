package com.silverpine.uu.core

fun Int.uuEpochTimeToSystemTime(): Long
{
    return this * 1000L
}