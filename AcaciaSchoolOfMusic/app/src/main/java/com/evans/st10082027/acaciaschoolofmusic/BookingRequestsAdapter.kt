package com.evans.st10082027.acaciaschoolofmusic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class BookingRequestsAdapter(private val bookingRequestsList: MutableList<BookingRequest>) :
    RecyclerView.Adapter<BookingRequestsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.booking_request_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookingRequest = bookingRequestsList[position]

        holder.username.text = bookingRequest.username
        holder.instrument.text = bookingRequest.instrument
        holder.selectedDays.text = bookingRequest.selectedDaysList
        holder.selectedTimes.text = bookingRequest.selectedTimesList
        holder.address.text = bookingRequest.address
        holder.confirmed.text = if (bookingRequest.confirmed) "Confirmed" else "Pending"

        holder.btnConfirm.setOnClickListener {
            bookingRequestsList[position].confirmed = true
            notifyDataSetChanged()
            updateConfirmedStatus(bookingRequest.entryId, true)
        }

        holder.btnDeny.setOnClickListener {
            bookingRequestsList.removeAt(position)
            notifyDataSetChanged()
            removeEntryFromDatabase(bookingRequest.entryId)
        }
    }

    override fun getItemCount(): Int {
        return bookingRequestsList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.username)
        val instrument: TextView = itemView.findViewById(R.id.instrument)
        val selectedDays: TextView = itemView.findViewById(R.id.selectedDays)
        val selectedTimes: TextView = itemView.findViewById(R.id.selectedTimes)
        val address: TextView = itemView.findViewById(R.id.address)
        val confirmed: TextView = itemView.findViewById(R.id.confirmedStatus)

        val btnConfirm: Button = itemView.findViewById(R.id.btnConfirm)
        val btnDeny: Button = itemView.findViewById(R.id.btnDeny)
    }

    private fun updateConfirmedStatus(entryId: String, confirmed: Boolean) {
        val entryRef = FirebaseDatabase.getInstance().reference.child("bookingRequests").child(entryId)
        entryRef.child("confirmed").setValue(confirmed)
    }

    private fun removeEntryFromDatabase(entryId: String) {
        val entryRef = FirebaseDatabase.getInstance().reference.child("bookingRequests").child(entryId)
        entryRef.removeValue()
    }
}
