package org.mabartos.meetmethere.util

import android.content.Context
import android.text.Editable
import android.text.format.DateFormat
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.function.Consumer

fun Context.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.datePicker(
    fragmentManager: FragmentManager,
    title: String = "Set date",
    startSelection: Long = MaterialDatePicker.todayInUtcMilliseconds(),
    onPositiveClick: Consumer<Calendar>
) {
    val picker = MaterialDatePicker.Builder.datePicker()
        .setTitleText(title)
        .setSelection(startSelection)
        .build()

    picker.addOnPositiveButtonClickListener { selection ->
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selection

        onPositiveClick.accept(calendar)
    }

    picker.show(fragmentManager, picker.tag)
}

fun Context.timePicker(
    fragmentManager: FragmentManager,
    onPositiveClick: Consumer<LocalTime>,
    title: String = "Set time",
    startSelection: LocalDateTime = LocalDateTime.now(),
    system24Hour: Boolean = DateFormat.is24HourFormat(this)
) {
    val clockFormat = if (system24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

    val picker = MaterialTimePicker.Builder()
        .setTimeFormat(clockFormat)
        .setTitleText(title)
        .setHour(startSelection.hour)
        .setMinute(startSelection.minute)
        .build()

    picker.show(fragmentManager, picker.tag)

    picker.addOnPositiveButtonClickListener {
        val time: LocalTime = LocalTime.of(picker.hour, picker.minute)
        onPositiveClick.accept(time)
    }
}

fun Context.formatDate(pattern: String, date: Date, locale: Locale = Locale.getDefault()): String {
    return try {
        val outputDateFormat = SimpleDateFormat(pattern, locale)
        outputDateFormat.format(date)
    } catch (e: Throwable) {
        print("Cannot format date")
        "N/A"
    }
}

fun Context.formatDate(
    pattern: String,
    date: LocalDateTime,
    locale: Locale = Locale.getDefault()
): String {
    return try {
        val outputDateFormat = DateTimeFormatter.ofPattern(pattern, locale)
        date.format(outputDateFormat)
    } catch (e: Throwable) {
        print("Cannot format date")
        "N/A"
    }
}

fun Context.formatTime(
    time: LocalTime,
    system24Hour: Boolean = DateFormat.is24HourFormat(this)
): String {
    return try {
        val clockFormat = if (system24Hour) "HH:mm" else "hh:mm a"
        val formatter = DateTimeFormatter.ofPattern(clockFormat)
        time.format(formatter)
    } catch (e: Throwable) {
        print("Cannot format time")
        "N/A"
    }
}