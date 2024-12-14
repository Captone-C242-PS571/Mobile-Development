package com.project.sleepwell.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.project.sleepwell.R
import com.project.sleepwell.databinding.FragmentHomeBinding
import com.project.sleepwell.tflite.TFLiteModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var tfliteModel: TFLiteModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        tfliteModel = TFLiteModel(requireContext()) // Muat model di onCreateView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGenderCategorySpinner()
        setupBMICategorySpinner()
        setupSleepCategorySpinner()

        binding.submitButton.setOnClickListener {
            val gender = binding.genderCategoriesSpinner.selectedItem.toString()
            val age = binding.ageInput.text.toString().toIntOrNull() ?: 0
            val sleepDuration = binding.sleepDurationInput.text.toString().toFloatOrNull() ?: 0f
            val physicalActivity = binding.physicalActivityInput.text.toString().toFloatOrNull() ?: 0f
            val stressLevel = binding.stressLevelInput.text.toString().toIntOrNull() ?: 0
            val bmiCategory = binding.bmiCategorySpinner.selectedItem.toString()
            val heartRate = binding.heartRateInput.text.toString().toIntOrNull() ?: 0
            val dailySteps = binding.dailyStepsInput.text.toString().toIntOrNull() ?: 0
            val sleepDisorder = binding.sleepDisorderSpinner.selectedItem.toString()

            val genderEncoded = when (gender) {
                "Female" -> 0
                "Male" -> 1
                else -> -1
            }

            val bmiCategoryEncoded = when (bmiCategory) {
                "Normal" -> 0
                "Normal Weight" -> 1
                "Obese" -> 2
                "Overweight" -> 3
                else -> -1
            }

            val sleepDisorderEncoded = when (sleepDisorder) {
                "Insomnia" -> 0
                "None" -> 1
                "Sleep Apnea" -> 2
                else -> -1
            }

            // Scaler: Mean and Std (gunakan nilai dari dataset)
            val mean = floatArrayOf(0.5f, 42.18f, 7.13f, 59.17f, 5.39f, 1.30f, 70.17f, 6816.84f, 1.00f)
            val std = floatArrayOf(0.5f, 8.67f, 0.80f, 20.83f, 1.77f, 1.43f, 4.14f, 1617.91f, 0.64f)

            // Input array
            val userInput = floatArrayOf(
                genderEncoded.toFloat(),
                age.toFloat(),
                sleepDuration,
                physicalActivity,
                stressLevel.toFloat(),
                bmiCategoryEncoded.toFloat(),
                heartRate.toFloat(),
                dailySteps.toFloat(),
                sleepDisorderEncoded.toFloat()
            )

            // Scaling input
            val scaledInput = FloatArray(userInput.size) { i ->
                (userInput[i] - mean[i]) / std[i]
            }

            // Debug logs
            println("Original Input: ${userInput.joinToString()}")
            println("Scaled Input: ${scaledInput.joinToString()}")

            val output = tfliteModel.predict(scaledInput)

            val bundle = Bundle()
            bundle.putFloat("SLEEP_DURATION_REC", sleepDuration)
            bundle.putFloat("PHYSICAL_ACTIVITY_REC", physicalActivity)
            bundle.putInt("STRESS_LEVEL_REC", stressLevel)
            bundle.putInt("DAILY_STEPS", dailySteps)
            bundle.putFloatArray("OUTPUT", output)
            findNavController().navigate(R.id.navigation_results, bundle)
        }
    }

    private fun setupGenderCategorySpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_categories,
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.genderCategoriesSpinner.adapter = adapter

        // Set a listener for item selection
        binding.genderCategoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Handle item selection
                val selectedCategory = parent?.getItemAtPosition(position).toString()
                Toast.makeText(requireContext(), "Selected: $selectedCategory", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case where no item is selected, if needed
            }
        }
    }

    private fun setupBMICategorySpinner() {
        // Create an adapter for the spinner using the string-array from resources
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.bmi_categories, // String array in res/values/strings.xml
            android.R.layout.simple_spinner_dropdown_item
        )

        // Attach the adapter to the spinner
        binding.bmiCategorySpinner.adapter = adapter

        // Set a listener for item selection
        binding.bmiCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Handle item selection
                val selectedCategory = parent?.getItemAtPosition(position).toString()
                Toast.makeText(requireContext(), "Selected: $selectedCategory", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case where no item is selected, if needed
            }
        }
    }

    private fun setupSleepCategorySpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sleep_categories,
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.sleepDisorderSpinner.adapter = adapter

        // Set a listener for item selection
        binding.sleepDisorderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Handle item selection
                val selectedCategory = parent?.getItemAtPosition(position).toString()
                Toast.makeText(requireContext(), "Selected: $selectedCategory", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case where no item is selected, if needed
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tfliteModel.close()
        _binding = null
    }
}
