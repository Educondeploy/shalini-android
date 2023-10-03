package com.tt.skolarrs.utilz

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DateDifference {

    companion object {
        @Throws(ParseException::class)
        fun getDateDifference(inputDateString: String?): String? {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")

            val inputDate = dateFormat.parse(inputDateString)
            val currentDate = Date()
            val diffInMilliseconds = currentDate.time - inputDate.time
            val diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(diffInMilliseconds)
            val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMilliseconds)
            val diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMilliseconds)
            val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMilliseconds)
            return if (diffInSeconds < 60) {
                diffInSeconds.toString() + " seconds ago"
            } else if (diffInMinutes == 1L) {
                "1 minute ago"
            } else if (diffInMinutes < 60) {
                "$diffInMinutes minutes ago"
            } else if (diffInHours == 1L) {
                "1 hour ago"
            } else if (diffInHours < 24) {
                "$diffInHours hours ago"
            } else if (diffInDays == 1L) {
                "1 day ago"
            } else if (diffInDays < 7) {
                "$diffInDays days ago"
            } else if (diffInDays == 7L) {
                "1 week ago"
            } else if (diffInDays < 30) {
                (diffInDays / 7).toString() + " weeks ago"
            } else if (diffInDays == 30L) {
                "1 month ago"
            } else {
                (diffInDays / 30).toString() + " months ago"
            }
        }

        @Throws(ParseException::class)
        fun getDate(inputDateString: String?): Long {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val inputDate = dateFormat.parse(inputDateString)
            val currentDate = Date()
            val diffInMilliseconds = currentDate.time - inputDate.time
            val diffInSeconds =
                TimeUnit.MILLISECONDS.toSeconds(diffInMilliseconds)
            val diffInMinutes =
                TimeUnit.MILLISECONDS.toMinutes(diffInMilliseconds)
            val diffInHours =
                TimeUnit.MILLISECONDS.toHours(diffInMilliseconds)
            return TimeUnit.MILLISECONDS.toDays(diffInMilliseconds)
        }
    }
}