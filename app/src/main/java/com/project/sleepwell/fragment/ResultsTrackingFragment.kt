package com.project.sleepwell.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.sleepwell.databinding.FragmentResultsTrackingBinding
import com.project.sleepwell.utils.alarm.AlarmInterval
import kotlin.math.roundToInt

class ResultsTrackingFragment : Fragment() {

    private var _binding: FragmentResultsTrackingBinding? = null
    private val binding get() = _binding!!

    private lateinit var intervals: ArrayList<AlarmInterval>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResultsTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val outputArray = arguments?.getFloatArray("OUTPUT")
        val sleepDuration = arguments?.getFloat("SLEEP_DURATION_REC", 0f) ?: 0f
        val physicalActivity = arguments?.getFloat("PHYSICAL_ACTIVITY_REC", 0f) ?: 0f
        val stressLevel = arguments?.getInt("STRESS_LEVEL_REC", 0) ?: 0
        val dailySteps = arguments?.getInt("DAILY_STEPS", 0) ?: 0

        if (outputArray != null) {
            val roundedValue = outputArray[0].roundToInt()
            binding.circularProgress.progress = roundedValue.toFloat()

            // Tampilkan pesan berdasarkan nilai
            val congratulationMessage = if (roundedValue > 6) {
                "Selamat kualitas tidur kamu bagus!"
            } else {
                "Sayang sekali"
            }

            val sleepDurationMessage = if (sleepDuration < 7) {
                "Tingkatkan Kualitas Tidur"
            } else {
                "Pertahankan Kualitas Tidur"
            }

            val physicalActivityMessage = if (physicalActivity > 120) {
                "Kurangi Aktivitas Fisik"
            } else if (physicalActivity < 60) {
                "Tingkatkan Aktivitas Fisik"
            } else {
                "Pertahankan Aktivitas Fisik"
            }

            val stressLevelMessage = if (stressLevel > 3) {
                "Kurangi Tingkat Stres"
            } else if (stressLevel < 2) {
                "Tingkatkan Tingkat Stres"
            } else {
                "Pertahankan Tingkat Stres"
            }

            val dailyStepsMessage = if (dailySteps > 10000) {
                "Kurangi Jumlah Langkah"
            } else if (dailySteps < 5000) {
                "Tingkatkan Jumlah Langkah"
            } else {
                "Pertahankan Jumlah Langkah"
            }

            // Set teks pada TextView untuk menampilkan pesan
            binding.congratulationText.text = congratulationMessage
            binding.sleepDurationRec.text = sleepDurationMessage
            binding.physicalActivityRec.text = physicalActivityMessage
            binding.stressLevelRec.text = stressLevelMessage
            binding.dailyStepsRec.text = dailyStepsMessage
        } else {
            println("OutputArray is null")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Avoid memory leaks by nullifying the binding reference
        _binding = null
    }

    companion object {
        // Helper to create instance with arguments
        fun newInstance(score: Int): ResultsTrackingFragment {
            val fragment = ResultsTrackingFragment()
            val args = Bundle()
            args.putInt("SCORE", score)
            fragment.arguments = args
            return fragment
        }
    }
}