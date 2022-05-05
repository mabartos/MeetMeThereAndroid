package org.mabartos.meetmethere.util

import android.text.Editable
import com.google.android.material.textfield.TextInputLayout

class InputUtils {

    companion object {
        fun errorOnBlankField(
            input: Editable?,
            errorTextLayout: TextInputLayout,
            errorMessage: String = "You need to specify this field"
        ): Boolean {
            if (input == null || input.isBlank()) {
                errorTextLayout.error = errorMessage
                return true
            } else {
                errorTextLayout.error = ""
            }
            return false
        }
    }

}