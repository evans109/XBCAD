package com.evans.st10082027.acaciaschoolofmusic

import androidx.lifecycle.ViewModel

class TeacherAvailabilityViewModel : ViewModel() {
    private var selectedDays: MutableSet<String> = mutableSetOf()
    private var selectedTimes: MutableSet<String> = mutableSetOf()

    fun setSelectedDays(days: Set<String>) {
        selectedDays.clear()
        selectedDays.addAll(days)
    }

    fun getSelectedDays(): Set<String> {
        return selectedDays
    }

    fun setSelectedTimes(times: Set<String>) {
        selectedTimes.clear()
        selectedTimes.addAll(times)
    }

    fun getSelectedTimes(): Set<String> {
        return selectedTimes
    }

    fun removeSelectedDay(selectedDay: String) {
        selectedDays.remove(selectedDay)
    }

    fun addSelectedDay(selectedDay: String) {
        selectedDays.add(selectedDay)
    }

    fun removeSelectedTime(time: String) {
        selectedTimes.remove(time)
    }

    fun addSelectedTime(time: String) {
        selectedTimes.add(time)
    }
}
