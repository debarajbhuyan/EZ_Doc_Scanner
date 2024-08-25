package creations.maa.devraj.ezdocscanner.data.local.converters

import androidx.room.TypeConverter
import java.util.Date

class DateTypeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun DateToTimestamp(date: Date): Long {
        return date.time
    }
}