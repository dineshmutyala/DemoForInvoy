package com.dinesh.demoforinvoy.core

import timber.log.Timber
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SynchronizedTimeUtils {
    companion object {

        private const val FORMAT_DAY_OF_THE_WEEK = "EEEE"
        private const val FORMAT_SHORT_DAY_OF_THE_WEEK = "EEE"

        private const val FORMAT_SHORT_MONTH_DATE = "MMM d"

        private const val FORMAT_SHORT_DAY_AND_LONG_MONTH_DATE_YEAR = "EEE, MMMM d yyyy"

        private const val FORMAT_LONG_DAY_AND_MONTH_DATE = "EEEE, MMM. d"

        private const val FORMAT_DATE_SLASHED_MDY = "MM/dd/yyyy"

        private val calendar = Calendar.getInstance()

        private val mapFormatToDateFormat = mutableMapOf<String, DateFormat>()

        @Synchronized
        fun getFormattedDayOfTheWeek(date: Date, timezone: TimeZone): String {
            return applyTimeZoneAndFormat(FORMAT_DAY_OF_THE_WEEK, timezone, date)
        }

        @Synchronized
        fun getFormattedShortDayOfTheWeek(date: Date, timezone: TimeZone): String {
            return applyTimeZoneAndFormat(FORMAT_SHORT_DAY_OF_THE_WEEK, timezone, date)
        }

        @Synchronized
        fun getFormattedLongDayAndMonthWithDateNoSuffix(date: Date, timezone: TimeZone): String {
            return applyTimeZoneAndFormat(FORMAT_LONG_DAY_AND_MONTH_DATE, timezone, date)
        }

        @Synchronized
        fun getFormattedDateSlashedMDY(date: Date, timezone: TimeZone): String {
            return applyTimeZoneAndFormat(FORMAT_DATE_SLASHED_MDY, timezone, date)
        }

        @Synchronized
        fun getFormattedShortMonthAndDate(date: Date, timezone: TimeZone): String {
            return applyTimeZoneAndFormat(FORMAT_SHORT_MONTH_DATE, timezone, date)
        }

        @Synchronized
        fun getFormattedShortDayLongMonthDayAndYear(date: Date, timezone: TimeZone): String {
            return applyTimeZoneAndFormat(FORMAT_SHORT_DAY_AND_LONG_MONTH_DATE_YEAR, timezone, date)
        }

        @Synchronized
        fun parseDateSlashedMDY(dateString: String): Date? {
            return tryParseDateTime(getDateFormat(FORMAT_DATE_SLASHED_MDY), dateString)
        }

        @Synchronized
        private fun tryParseDateTime(dateFormatter: DateFormat, dateTime: String) = dateFormatter.parseOrNull(dateTime)

        @Synchronized
        private fun formatDateTime(dateFormatter: DateFormat, dateTime: Date): String = dateFormatter.format(dateTime)

        @Synchronized
        private fun getDateFormat(format: String, locale: Locale? = null): DateFormat =
            mapFormatToDateFormat[format] ?: createAndAddDateFormat(format, locale)

        private fun createAndAddDateFormat(format: String, locale: Locale? = null): DateFormat =
            SimpleDateFormat(format, locale ?: Locale.getDefault()).also {
                mapFormatToDateFormat[format] = it
            }

        private fun applyTimeZoneAndFormat(format: String, timezone: TimeZone, date: Date) =
            applyTimeZoneAndFormat(getDateFormat(format), timezone, date)

        private fun applyTimeZoneAndFormat(dateFormat: DateFormat, timezone: TimeZone, date: Date) =
            formatDateTime(dateFormat.apply { timeZone = timezone }, date)

        private fun getDayOfTheMonth(date: Date, timezone: TimeZone) =
            calendar.apply {
                timeZone = timezone
                time = date
            }.get(Calendar.DATE)

        private fun DateFormat.parseOrNull(source: String): Date? {
            try {
                return this.parse(source)
            } catch (parseException: ParseException) {
                Timber.e(parseException)
            }
            return null
        }
    }
}