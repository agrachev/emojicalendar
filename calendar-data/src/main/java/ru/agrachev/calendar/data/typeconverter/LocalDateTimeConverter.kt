package ru.agrachev.calendar.data.typeconverter

import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDateTimeConverter {
    @TypeConverter
    fun toDate(dateString: String): LocalDate = LocalDate.parse(dateString)

    @TypeConverter
    fun toDateString(date: LocalDate) = date.toString()
}
