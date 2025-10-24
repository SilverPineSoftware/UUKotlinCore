import com.silverpine.uu.core.UUDate
import com.silverpine.uu.core.uuFormatDate
import com.silverpine.uu.core.uuParseDate
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object UUDateJsonAdapter: KSerializer<Date>
{
    private val format: String = UUDate.Formats.RFC_3339_WITH_MILLIS
    private val timeZone: TimeZone = UUDate.TimeZones.UTC
    private val locale: Locale = Locale.US

    override val descriptor = PrimitiveSerialDescriptor("UUDateJsonAdapter", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Date)
    {
        encoder.encodeString(value.uuFormatDate(format, timeZone, locale))
    }

    override fun deserialize(decoder: Decoder): Date
    {
        return decoder.decodeString().uuParseDate(format, timeZone, locale) ?: Date(0)
    }
}