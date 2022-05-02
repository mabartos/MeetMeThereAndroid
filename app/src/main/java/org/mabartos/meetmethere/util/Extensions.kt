package org.mabartos.meetmethere.util

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
fun <T> Context.datePicker(
    fragmentManager: FragmentManager,
    title: String = "Set date",
    selection: Long = MaterialDatePicker.todayInUtcMilliseconds(),
    onPositiveClick: Consumer<Long>
) {
    val picker = MaterialDatePicker.Builder.datePicker()
        .setTitleText(title)
        .setSelection(selection)
        .build()

    picker.addOnPositiveButtonClickListener { selection ->
        onPositiveClick.accept(selection)
    }

    picker.show(fragmentManager, picker.toString())
}