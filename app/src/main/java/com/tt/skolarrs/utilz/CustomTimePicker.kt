package com.tt.skolarrs.utilz

import android.app.TimePickerDialog
import android.content.Context
import android.widget.TimePicker
import java.util.*

class CustomTimePicker(
    private val context: Context,
    private val onTimeSetListener: TimePickerDialog.OnTimeSetListener,
    private val hour: Int,
    private val minute: Int,
    private val is24HourFormat: Boolean
) {

    fun show() {
        val timePickerDialog = TimePickerDialog(
            context,
            TimePickerDialog.OnTimeSetListener { view: TimePicker, hourOfDay: Int, minute: Int ->
                // Create a Calendar instance to set the selected hour and minute
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val formattedTime: String

                if (is24HourFormat) {
                    formattedTime = String.format("%02d:%02d", hourOfDay, minute)
                } else {
                    val amPm = if (hourOfDay < 12) "AM" else "PM"
                    val hourIn12HourFormat = if (hourOfDay > 12) hourOfDay - 12 else hourOfDay
                    formattedTime = String.format("%02d:%02d %s", hourIn12HourFormat, minute, amPm)
                }
                // Call the onTimeSetListener with the formatted time
                onTimeSetListener.onTimeSet(view, hourOfDay, minute)
            },
            hour,
            minute,
            is24HourFormat
        )
        timePickerDialog.show()
    }
}