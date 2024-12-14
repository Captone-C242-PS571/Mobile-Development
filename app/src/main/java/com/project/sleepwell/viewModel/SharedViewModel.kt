package com.project.sleepwell.viewModel

import androidx.lifecycle.ViewModel
import com.project.sleepwell.utils.alarm.AlarmInterval

class SharedViewModel : ViewModel() {
    var intervalList: List<AlarmInterval> = arrayListOf(
        AlarmInterval(7, 30, "Wake up"),
        AlarmInterval(7, 30, "Wake up"),
        AlarmInterval(7, 30, "Wake up"),
        AlarmInterval(7, 30, "Wake up")
    )
}