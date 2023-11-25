package com.evans.st10082027.acaciaschoolofmusic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evans.st10082027.acaciaschoolofmusic.databinding.FragmentMyRequestsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyRequestsFragment : Fragment() {

    private lateinit var binding: FragmentMyRequestsBinding
    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var myRequestsList: MutableList<MyRequests>
    private lateinit var adapter: MyRequestsAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyRequestsBinding.inflate(inflater, container, false)
        val view = binding.root

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        myRequestsList = mutableListOf()
        databaseRef = FirebaseDatabase.getInstance().reference.child("bookingRequests")
        auth = FirebaseAuth.getInstance()

        adapter = MyRequestsAdapter(myRequestsList)
        recyclerView.adapter = adapter

        fetchBookingRequests()

        binding.viewProfileButton.setOnClickListener {
            loadFragment(ProfileFragment())
        }

        return view
    }

    private fun fetchBookingRequests() {
        myRequestsList.clear()

        val currentUserEmail = auth.currentUser?.email

        currentUserEmail?.let { email ->
            val query: Query = databaseRef.orderByChild("username").equalTo(email)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val username = snapshot.child("username").value.toString()
                        val instrument = snapshot.child("instrument").value.toString()
                        val selectedDaysList = snapshot.child("selectedDaysList").value.toString()
                        val selectedTimesList = snapshot.child("selectedTimesList").value.toString()
                        val address = snapshot.child("address").value.toString()
                        val confirmed = snapshot.child("confirmed").value as? Boolean ?: false

                        val myRequests = MyRequests(
                            snapshot.key.toString(),
                            username,
                            instrument,
                            selectedDaysList,
                            selectedTimesList,
                            address,
                            confirmed
                        )
                        myRequestsList.add(myRequests)
                    }
                    updateAdapter()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(requireContext(), "Error connecting to the database", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun updateAdapter() {
        adapter.notifyDataSetChanged()
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
