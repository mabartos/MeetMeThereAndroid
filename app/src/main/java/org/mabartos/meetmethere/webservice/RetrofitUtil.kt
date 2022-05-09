package org.mabartos.meetmethere.webservice

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitUtil {
    //TODO
    const val BASE_URL = "http://www.something.com"

    inline fun <reified T> createAqiWebService(url: String): T =
        create(url, createOkHttpClient())

    inline fun <reified T> createAqiWebService(): T =
        createAqiWebService(BASE_URL)

    inline fun <reified T> create(baseUrl: String, okHttpClient: OkHttpClient): T =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(T::class.java)

    fun createOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder().apply {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            addInterceptor(logging)
        }.build()

    fun <T> callback(
        supplier: () -> Call<T>,
        onSuccess: (T) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        supplier.invoke().enqueue(object : Callback<T> {

            override fun onResponse(
                call: Call<T>,
                response: Response<T>
            ) {
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