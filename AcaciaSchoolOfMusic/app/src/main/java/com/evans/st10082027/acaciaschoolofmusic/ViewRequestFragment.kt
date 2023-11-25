package com.evans.st10082027.acaciaschoolofmusic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*

class ViewRequestFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var bookingRequestsList: MutableList<BookingRequest>
    private lateinit var adapter: BookingRequestsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_request, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        bookingRequestsList = mutableListOf()
        databaseRef = FirebaseDatabase.getInstance().reference.child("bookingRequests")
        auth = FirebaseAuth.getInstance()

        adapter = BookingRequestsAdapter(bookingRequestsList)
        recyclerView.adapter = adapter

        fetchBookingRequests()

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = requireActivity() as MainActivity
        mainActivity.showBottomNavigation(true)

    }

    private fun updateAdapter() {
        adapter.notifyDataSetChanged()
    }

    private fun fetchBookingRequests() {
        bookingRequestsList.clear()

        val query: Query = databaseRef

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val username = snapshot.child("username").value.toString()
                    val instrument = snapshot.child("instrument").value.toString()
                    val selectedDaysList = snapshot.child("selectedDaysList").value.toString()
                    val selectedTimesList = snapshot.child("selectedTimesList").value.toString()
                    val address = snapshot.child("address").value.toString()
                    val confirmed = snapshot.child("confirmed").value as? Boolean ?: false

                    val bookingRequest = BookingRequest(
                        snapshot.key.toString(),
                        username,
                        instrument,
                        selectedDaysList,
                        selectedTimesList,
                        address,
                        confirmed
                    )
                    bookingRequestsList.add(bookingRequest)
                }
                updateAdapter()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Error connecting to the database", Toast.LENGTH_SHORT).show()
            }
        })
    }


}