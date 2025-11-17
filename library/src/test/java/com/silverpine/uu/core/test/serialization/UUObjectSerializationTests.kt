package com.silverpine.uu.core.test.serialization

import com.silverpine.uu.core.serialization.UUEnumFormat
import com.silverpine.uu.core.serialization.UUEnumSerializer
import com.silverpine.uu.core.serialization.UUSafeEnumSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


enum class TestStatus
{
    New,
    InProgress,
    Done
}

object TestStatusSnakeCaseSerializer :
    UUSafeEnumSerializer<TestStatus>(
        TestStatus::class.java,
        UUEnumFormat.NameSnakeCase,
        defaultDeserializeValue = TestStatus.New
    )

object NullableTestStatusOrdinalSerializer :
    UUEnumSerializer<TestStatus>(
        TestStatus::class.java,
        UUEnumFormat.Ordinal,
        defaultDeserializeValue = null
    )

@Serializable
data class Task(
    @Serializable(with = TestStatusSnakeCaseSerializer::class)
    val status: TestStatus,

    @Serializable(with = NullableTestStatusOrdinalSerializer::class)
    val previousStatus: TestStatus? = null
)

class UUObjectSerializationTests
{
    private val json = Json { encodeDefaults = true }

    @Nested
    inner class Serialize
    {
        @Test
        fun `serializes snake_case and ordinal correctly`()
        {
            val task = Task(
                status = TestStatus.InProgress,
                previousStatus = TestStatus.New
            )

            val encoded = json.encodeToString(task)
            assertEquals("""{"status":"in_progress","previousStatus":0}""", encoded)
        }

        @Test
        fun `serializes null previousStatus`()
        {
            val task = Task(
                status = TestStatus.Done,
                previousStatus = null
            )

            val encoded = json.encodeToString(task)
            assertEquals("""{"status":"done","previousStatus":null}""", encoded)
        }
    }

    @Nested
    inner class Deserialize
    {
        @Test
        fun `deserializes valid snake_case and ordinal`()
        {
            val input = """{"status":"in_progress","previousStatus":0}"""
            val result = json.decodeFromString<Task>(input)

            assertEquals(TestStatus.InProgress, result.status)
            assertEquals(TestStatus.New, result.previousStatus)
        }

        @Test
        fun `deserializes null previousStatus`()
        {
            val input = """{"status":"done","previousStatus":null}"""
            val result = json.decodeFromString<Task>(input)

            assertEquals(TestStatus.Done, result.status)
            assertNull(result.previousStatus)
        }

        @Test
        fun `falls back to default on unknown status`()
        {
            val input = """{"status":"not_a_status","previousStatus":1}"""
            val result = json.decodeFromString<Task>(input)

            assertEquals(TestStatus.New, result.status) // fallback
            assertEquals(TestStatus.InProgress, result.previousStatus)
        }

        @Test
        fun `returns null for unknown ordinal in nullable field`()
        {
            val input = """{"status":"done","previousStatus":99}"""
            val result = json.decodeFromString<Task>(input)

            assertEquals(TestStatus.Done, result.status)
            assertNull(result.previousStatus)
        }
    }
}