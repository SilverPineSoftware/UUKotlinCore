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

class UUDateJsonAdapter(
    private val format: String = UUDate.RFC_3999_DATE_TIME_WITH_MILLIS_FORMAT,
    private val timeZone: TimeZone = UUDate.UTC_TIME_ZONE,
    private val locale: Locale = Locale.US): KSerializer<Date>
{
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