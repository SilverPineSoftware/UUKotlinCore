package com.silverpine.uu.core.test

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
open class TestModel
{
    @Json
    var id: String = ""

    @Json
    var name: String = ""

    @Json
    var level: Int = 0

    @Json
    var xp: Int = 0
}