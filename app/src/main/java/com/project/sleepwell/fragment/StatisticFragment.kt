package com.project.sleepwell.fragment

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.sleepwell.utils.alarm.AlarmInterval
import com.project.sleepwell.utils.alarm.AlarmService
import com.project.sleepwell.databinding.FragmentStatisticBinding
import com.project.sleepwell.ui.TrackingActivity
import com.project.sleepwell.viewModel.SharedViewModel

class StatisticFragment : Fragment() {
    private var _binding: FragmentStatisticBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val sharedPreferences: SharedPreferences
        get() = requireActivity().getSharedPreferences("alarm_preferences", Context.MODE_PRIVATE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val savedIntervals = loadIntervals()
        if (savedIntervals.isNotEmpty()) {
            sharedViewModel.intervalList = savedIntervals
        } else {
            val defaultInterval = AlarmInterval(10, 0, "It's time to Sleep!")
            sharedViewModel.intervalList = listOf(defaultInterval)
        }

        binding.startTracking.setOnClickListener {
            startActivity(Intent(requireContext(), TrackingActivity::class.java))
        }

        binding.buttonSetAlarm.setOnClickListener {
            val alarmIntent = Intent(requireContext(), AlarmService::class.java)
            AlarmService.scheduleAlarms(requireContext(), sharedViewModel.intervalList, alarmIntent)
            Toast.makeText(context, "Setting up alarm for ${printIntervals()}", Toast.LENGTH_LONG).show()
        }

        val alarmBind = AlarmBind(binding.icEditClock, binding.txtClock, sharedViewModel.intervalList[0])

        updateDisplayTxt(alarmBind.display, alarmBind.time)
        alarmBind.editBtn.setOnClickListener {
            val hour = alarmBind.time.hour
            val minute = alarmBind.time.minute

            val timePickerDialog = TimePickerDialog(
                requireContext(),
                { _, selectedHour, selectedMinute ->
                    alarmBind.time.hour = selectedHour
                    alarmBind.time.minute = selectedMinute
                    updateDisplayTxt(alarmBind.display, alarmBind.time)

                    sharedViewModel.intervalList = listOf(alarmBind.time)
                    saveIntervals(sharedViewModel.intervalList)
                },
                hour,
                minute,
                false
            )
            timePickerDialog.show()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun printIntervals(): String {
        val uniqueIntervals = sharedViewModel.intervalList.distinct()

        val formattedIntervals = uniqueIntervals.joinToString(", ") { interval ->
            val hour = if (interval.hour % 12 == 0) 12 else interval.hour % 12
            val amPm = if (interval.hour < 12) "AM" else "PM"
            String.format("%02d:%02d %s", hour, interval.minute, amPm)
        }

        return formattedIntervals
    }

    @SuppressLint("DefaultLocale")
    private fun updateDisplayTxt(display: TextView, time: AlarmInterval) {
        val hour = if (time.hour % 12 == 0) 12 else time.hour % 12
        val amPm = if (time.hour < 12) "am" else "pm"
        val formattedTime = String.format("%02d:%02d %s", hour, time.minute, amPm)
        display.text = formattedTime
    }

    private fun saveIntervals(intervalList: List<AlarmInterval>) {
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(intervalList)
        editor.putString("intervals", json)
        editor.apply()
    }

    private fun loadIntervals(): List<AlarmInterval> {
        val json = sharedPreferences.getString("intervals", null)
        val type = object : TypeToken<List<AlarmInterval>>() {}.type
        return if (json != null) {
            Gson().fromJson(json, type)
        } else {
            emptyList()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private class AlarmBind(
    val editBtn: ImageView,
    val display: TextView,
    val time: AlarmInterval
)