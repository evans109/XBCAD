package com.evans.st10082027.acaciaschoolofmusic

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.evans.st10082027.acaciaschoolofmusic.databinding.FragmentTeacherBinding
import com.google.android.material.internal.FlowLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView


class TeacherFragment : Fragment() {

    private lateinit var binding: FragmentTeacherBinding
    private lateinit var teacherAvailabilityViewModel: TeacherAvailabilityViewModel
    private lateinit var bottomNavigationView: BottomNavigationView // Assuming your bottom navigation view is of type BottomNavigationView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeacherBinding.inflate(inflater)

        teacherAvailabilityViewModel = ViewModelProvider(this).get(TeacherAvailabilityViewModel::class.java)

        setupCalendar()
        setupTimeSlots()

        binding.saveAvailabilityButton.setOnClickListener {
            saveAvailabilityToFirebase()
        }

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = requireActivity() as MainActivity
        mainActivity.showBottomNavigation(true)
    }


    private fun saveAvailabilityToFirebase() {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        if (currentUserEmail != null) {
            val encodedEmail = encodeEmail(currentUserEmail)

            val availabilityData = mapOf(
                "username" to currentUserEmail,
                "selectedDays" to teacherAvailabilityViewModel.getSelectedDays().toList(),
                "selectedTimes" to teacherAvailabilityViewModel.getSelectedTimes().toList()
            )

            val availabilityRef = Firebase.database.reference.child("availabilities").child(encodedEmail)

            availabilityRef.setValue(availabilityData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Availability saved successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("TeacherFragment", "Error saving availability to Firebase: ${e.message}", e)
                    Toast.makeText(requireContext(), "Failed to save availability", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun encodeEmail(email: String): String {
        return android.util.Base64.encodeToString(email.toByteArray(), android.util.Base64.NO_WRAP)
    }

    private fun setupCalendar() {
        val gridView: GridView = binding.calendarGridView

        val daysOfMonth = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31")

        val adapter = CalendarGridAdapter(requireContext(), daysOfMonth, teacherAvailabilityViewModel.getSelectedDays())
        gridView.adapter = adapter

        gridView.setOnItemClickListener { _, _, position, _ ->
            val selectedDay = daysOfMonth[position]
            if (teacherAvailabilityViewModel.getSelectedDays().contains(selectedDay)) {
                teacherAvailabilityViewModel.removeSelectedDay(selectedDay)
            } else {
                teacherAvailabilityViewModel.addSelectedDay(selectedDay)
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun setupTimeSlots() {
        val flowLayout: FlowLayout = binding.timeSlotsFlowLayout

        val timeSlots = arrayOf("1 PM", "2 PM", "3 PM", "4 PM", "5 PM", "6 PM", "7 PM", "8 PM")

        for (time in timeSlots) {
            val timeButton = createButton(time)
            flowLayout.addView(timeButton)

            timeButton.setOnClickListener {
                if (teacherAvailabilityViewModel.getSelectedTimes().contains(time)) {
                    teacherAvailabilityViewModel.removeSelectedTime(time)
                    timeButton.setBackgroundResource(android.R.color.transparent)
                } else {
                    teacherAvailabilityViewModel.addSelectedTime(time)
                    timeButton.setBackgroundResource(R.drawable.selected_time_background)
                }
            }

            if (teacherAvailabilityViewModel.getSelectedTimes().contains(time)) {
                timeButton.setBackgroundResource(R.drawable.selected_time_background)
            }
        }
    }

    private fun createButton(text: String): Button {
        val button = Button(requireContext())
        button.text = text
        button.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return button
    }
}
