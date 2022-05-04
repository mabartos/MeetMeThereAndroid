package org.mabartos.meetmethere.webservice

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.function.Supplier

object WebServiceUtil {

    fun <T> response(
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
}