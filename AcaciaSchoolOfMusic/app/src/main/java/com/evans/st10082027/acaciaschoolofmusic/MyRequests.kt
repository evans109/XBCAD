package com.evans.st10082027.acaciaschoolofmusic

data class MyRequests(
    val entryId: String,
    val username: String,
    val instrument: String,
    val selectedDaysList: String,
    val selectedTimesList: String,
    val address: String,
    var confirmed: Boolean
)
