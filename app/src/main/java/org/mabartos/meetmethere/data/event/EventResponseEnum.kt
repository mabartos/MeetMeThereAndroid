package org.mabartos.meetmethere.data.event

enum class EventResponseEnum(val textForm: String) {
    ACCEPT("accepted"),
    MAYBE("maybe"),
    DECLINE("declined"),
    NOT_ANSWERED("not_answered");

    companion object {
        fun getByTextForm(textForm: String): EventResponseEnum? {
            return values().find { i -> i.textForm == textForm }
        }
    }
}