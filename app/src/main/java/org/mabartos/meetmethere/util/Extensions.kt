package org.mabartos.meetmethere.util

import android.content.Context
import android.os.Build
import android.text.format.DateFormat
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.function.Consumer
import java.util.function.Supplier

fun Context.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

@RequiresApi(Build.VERSION_CODES.N)
fun <T> Context.response(
    supplier: Supplier<Call<T>>,
    onSuccess: (T) -> Unit,
    onFailure: (Throwable) -> Unit
) {
    supplier.get().enqueue(object : Callback<T> {

        override fun onResponse(call: Call<T>, response: Response<T>) {
            val responseBody = response.body()
            if (response.isSuccessful && responseBody != null) {
                onSuccess(responseBody)
            } else {
                onFailure(IllegalStateException("Response was not successful"))
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            onFailure(t)
        }
    })
}

@RequiresApi(Build.VERSION_CODES.N)
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

@RequiresApi(Build.VERSION_CODES.O)
fun Context.timePicker(
    fragmentManager: FragmentManager,
    context: Context,
    onPositiveClick: Consumer<LocalTime>,
    title: String = "Set time",
    startSelection: LocalDateTime = LocalDateTime.now(),
    system24Hour: Boolean = DateFormat.is24HourFormat(context)
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

@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
fun Context.formatTime(time: LocalTime, pattern: String = "HH:mm"): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        time.format(formatter)
    } catch (e: Throwable) {
        print("Cannot format time")
        "N/A"
    }
}