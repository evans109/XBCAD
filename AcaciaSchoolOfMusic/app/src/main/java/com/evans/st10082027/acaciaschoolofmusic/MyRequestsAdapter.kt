package com.evans.st10082027.acaciaschoolofmusic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class MyRequestsAdapter(private val myRequestsList: MutableList<MyRequests>) :
    RecyclerView.Adapter<MyRequestsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookingRequest = myRequestsList[position]

        holder.username.text = bookingRequest.username
        holder.instrument.text = bookingRequest.instrument
        holder.selectedDays.text = bookingRequest.selectedDaysList
        holder.selectedTimes.text = bookingRequest.selectedTimesList
        holder.address.text = bookingRequest.address
        holder.confirmed.text = if (bookingRequest.confirmed) "Confirmed" else "Pending"
        holder.btnDeny.text = if (bookingRequest.confirmed) "Cancel" else "Delete"

        holder.btnDeny.setOnClickListener {
            myRequestsList.removeAt(position)
            notifyDataSetChanged()
            removeEntryFromDatabase(bookingRequest.entryId)
        }
    }


    override fun getItemCount(): Int {
        return myRequestsList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.username)
        val instrument: TextView = itemView.findViewById(R.id.instrument)
        val selectedDays: TextView = itemView.findViewById(R.id.selectedDays)
        val selectedTimes: TextView = itemView.findViewById(R.id.selectedTimes)
        val address: TextView = itemView.findViewById(R.id.address)
        val confirmed: TextView = itemView.findViewById(R.id.confirmedStatus)
        val btnDeny: Button = itemView.findViewById(R.id.btnDeny)
    }

    private fun removeEntryFromDatabase(entryId: String) {
        val entryRef = FirebaseDatabase.getInstance().reference.child("bookingRequests").child(entryId)
        entryRef.removeValue()
    }
}
