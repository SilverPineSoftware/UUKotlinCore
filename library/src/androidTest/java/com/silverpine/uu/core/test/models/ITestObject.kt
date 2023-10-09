package com.silverpine.uu.core.test.models

interface ITestObject<T>
{
    fun default(): T
    fun min(): T
    fun max(): T
    fun random(): T
}