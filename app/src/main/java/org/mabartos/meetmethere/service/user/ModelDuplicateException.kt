package org.mabartos.meetmethere.service.user

class ModelDuplicateException : Exception {

    private var field: String = ""

    constructor() : super()

    constructor(message: String) : super(message)

    constructor(message: String, field: String) : super(message) {
        this.field = field
    }

    constructor(message: String, throwable: Throwable) : super(message, throwable)

    fun getField(): String = field
}