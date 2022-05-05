package org.mabartos.meetmethere.service

class ServiceUtil {

    companion object {
        fun <T> callback(
            supplier: () -> T,
            onSuccess: (T) -> Unit,
            onFailure: (Throwable) -> Unit
        ) {
            try {
                onSuccess(supplier.invoke())
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }
}