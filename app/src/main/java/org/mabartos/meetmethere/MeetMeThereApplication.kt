package org.mabartos.meetmethere

import android.app.Application
import com.google.android.material.color.DynamicColors

class MeetMeThereApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}