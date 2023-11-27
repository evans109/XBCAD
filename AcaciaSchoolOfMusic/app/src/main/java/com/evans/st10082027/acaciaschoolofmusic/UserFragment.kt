package com.evans.st10082027.acaciaschoolofmusic

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.evans.st10082027.acaciaschoolofmusic.databinding.FragmentUserBinding
import com.google.android.material.internal.FlowLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.*

class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private val availableDays = mutableSetOf<String>()
    private val selectedDays = mutableSetOf<String>()
    private val selectedTimes = mutableSetOf<String>()
    private val selectedTimeSlots = mutableSetOf<String>()

    private lateinit var databaseRef: DatabaseReference
    private lateinit var gridView: GridView
    private lateinit var adapter: UserCalendarGridAdapter

    var instrument = ""
    var address = ""
    var days = ""
    var times = ""
    var price = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater)
        val view = binding.root

        gridView = binding.calendarGridView

        databaseRef = FirebaseDatabase.getInstance().reference.child("availabilities")

        setupCalendar()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainActivity = requireActivity() as MainActivity
        mainActivity.showBottomNavigation(true)

        val guitarButton = view.findViewById<ImageButton>(R.id.guitarButton)
        val pianoButton = view.findViewById<ImageButton>(R.id.pianoButton)
        val drumsButton = view.findViewById<ImageButton>(R.id.drumsButton)
        val addressButton = view.findViewById<RadioButton>(R.id.addressRadioButton)
        val placeButton = view.findViewById<RadioButton>(R.id.placeRadioButton)
        val addressTextView = view.findViewById<EditText>(R.id.addressTextView)

        guitarButton.setOnClickListener {
            instrument = "Guitar"
            handleInstrumentButtonClick()
        }

        pianoButton.setOnClickListener {
            instrument = "Piano"
            handleInstrumentButtonClick()
        }

        drumsButton.setOnClickListener {
            instrument = "Drums"
            handleInstrumentButtonClick()
        }

        addressButton.setOnClickListener {
            addressTextView.visibility = View.VISIBLE
            binding.submitButton.visibility = View.VISIBLE
            price = "R120 p/40 minutes"
            binding.priceTextView.text = price
            binding.priceTextView.visibility = View.VISIBLE
        }

        placeButton.setOnClickListener {
            addressTextView.visibility = View.GONE
            binding.submitButton.visibility = View.VISIBLE
            address = "Acacia Location"
            price = "R80 p/40 minutes"
            binding.priceTextView.text = price
            binding.priceTextView.visibility = View.VISIBLE
        }

        binding.submitButton.setOnClickListener {
            saveSelectedData()
            saveBookingRequestToFirebase()
        }


    }

    private fun handleInstrumentButtonClick() {
        toggleGridViewVisibility()
        selectedTimes.clear()
    }

    private fun setupCalendar() {
        val daysOfMonth = arrayOf(
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"
        )

        adapter = UserCalendarGridAdapter(requireContext(), daysOfMonth, selectedDays, availableDays)
        gridView.adapter = adapter

        fetchAvailabilities()

        binding.timeSlotsFlowLayout.removeAllViews()
        hideRadioButtons()

        gridView.setOnItemClickListener { _, _, position, _ ->
            val selectedDay = daysOfMonth[position]

            if (availableDays.contains(selectedDay)) {
                selectedDays.clear()
                selectedDays.add(selectedDay)

                adapter.notifyDataSetChanged()
                highlightSelectedDays()

                if (selectedDays.isEmpty()) {
                    binding.timeSlotsFlowLayout.removeAllViews()
                    hideRadioButtons()
                } else {
                    showHideTimeSlots()
                }
            } else {
                Toast.makeText(requireContext(), "Please select a highlighted day", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun hideRadioButtons() {
        binding.placeRadioButton.visibility = View.GONE
        binding.addressRadioButton.visibility = View.GONE
    }

    private fun highlightSelectedDays() {
        gridView.post {
            for (i in 0 until gridView.childCount) {
                val gridItem = gridView.getChildAt(i)
                if (gridItem is LinearLayout) {
                    val dayText = gridItem.getChildAt(0) as? TextView
                    val day = dayText?.text.toString()

                    if (selectedDays.contains(day)) {
                        gridItem.setBackgroundResource(R.drawable.selected_day_background)
                    } else if (availableDays.contains(day)) {
                        gridItem.setBackgroundResource(R.drawable.selected_time_background)
                    } else {
                        gridItem.setBackgroundResource(0)
                    }
                }
            }
        }
    }

    private fun showHideTimeSlots() {
        binding.timeSlotsFlowLayout.removeAllViews()

        val timeSlots = arrayOf("1 PM", "2 PM", "3 PM", "4 PM", "5 PM", "6 PM", "7 PM", "8 PM")

        for (time in timeSlots) {
            val timeButton = createButton(time)
            binding.timeSlotsFlowLayout.addView(timeButton)

            timeButton.setOnClickListener {
                if (selectedTimeSlots.contains(time)) {
                    selectedTimeSlots.remove(time)
                    timeButton.setBackgroundColor(Color.TRANSPARENT)
                } else {
                    selectedTimeSlots.add(time)
                    timeButton.setBackgroundColor(Color.YELLOW)
                    showRadioButtons()
                }
            }

            if (selectedTimeSlots.contains(time)) {
                timeButton.setBackgroundColor(Color.YELLOW)
            }
        }
    }

    private fun showRadioButtons() {
        if (selectedTimeSlots.isNotEmpty()) {
            binding.placeRadioButton.visibility = View.VISIBLE
            binding.addressRadioButton.visibility = View.VISIBLE
            binding.placeRadioButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    binding.addressRadioButton.isChecked = false
                }
            }

            binding.addressRadioButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    binding.placeRadioButton.isChecked = false
                }
            }
        } else {
            binding.placeRadioButton.visibility = View.GONE
            binding.addressRadioButton.visibility = View.GONE
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

    private fun toggleGridViewVisibility() {
        if (gridView.visibility == View.VISIBLE) {
            gridView.visibility = View.GONE
        } else {
            gridView.visibility = View.VISIBLE
        }
    }

    private fun saveSelectedData() {
        days = selectedDays.toString()
        times = selectedTimeSlots.toString()

        if (binding.addressRadioButton.isChecked) {
            address = binding.addressTextView.text.toString()
        }

        println("Selected Days: $days")
        println("Selected Times: $times")
        println("Address: $address")
    }

    private fun fetchAvailabilities() {
        val database = Firebase.database
        val availabilitiesRef = database.getReference("availabilities")

        availabilitiesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                availableDays.clear()

                dataSnapshot.children.forEach { userSnapshot ->
                    userSnapshot.child("selectedDays").children.forEach { daySnapshot ->
                        val day = daySnapshot.getValue(String::class.java)
                        day?.let {
                            availableDays.add(it)
                        }
                    }

                    userSnapshot.child("selectedTimes").children.forEach { timeSnapshot ->
                        val time = timeSnapshot.getValue(String::class.java)
                        time?.let {
                            // Process selected times if needed
                        }
                    }
                }
                println("Available Days from Firebase: $availableDays")
                adapter.notifyDataSetChanged()
                highlightSelectedDays()
                showHideTimeSlots()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Error connecting to the database", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun saveBookingRequestToFirebase() {
        val auth: FirebaseAuth = Firebase.auth
        val currentUser = auth.currentUser

        val database = Firebase.database
        val bookingRequestsRef = database.getReference("bookingRequests")

        val userId = currentUser?.uid ?: ""

        val bookingDetails = HashMap<String, Any>()
        bookingDetails["username"] = currentUser?.email?: ""
        bookingDetails["instrument"] = instrument
        bookingDetails["selectedDaysList"] = days
        bookingDetails["selectedTimesList"] = times
        bookingDetails["address"] = address
        bookingDetails["confirmed"] = false

        bookingRequestsRef.child(userId).setValue(bookingDetails)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Booking request saved!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to save booking request!", Toast.LENGTH_SHORT).show()
            }
    }



}
