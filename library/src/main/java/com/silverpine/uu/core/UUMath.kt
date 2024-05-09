package com.silverpine.uu.core

fun Number.uuCelsiusToFahrenheit(): Double
{
    return (this.toDouble() * 9.0 / 5.0) + 32.0
}

fun Number.uuFahrenheitToCelsius(): Double
{
    return (this.toDouble() - 32.0) * 5.0 / 9.0
}