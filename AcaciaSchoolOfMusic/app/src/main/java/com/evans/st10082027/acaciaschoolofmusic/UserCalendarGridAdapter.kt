package com.evans.st10082027.acaciaschoolofmusic

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListAdapter
import android.widget.TextView

class UserCalendarGridAdapter(
    private val context: Context,
    private val daysOfMonth: Array<String>,
    private val selectedDays: Set<String>,
    private val availableDays: Set<String>
) : BaseAdapter(), ListAdapter {

    override fun getCount(): Int {
        return daysOfMonth.size
    }

    override fun getItem(position: Int): Any {
        return daysOfMonth[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val textView = TextView(context)
        textView.text = daysOfMonth[position]
        textView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER

        if (selectedDays.contains(daysOfMonth[position])) {
            textView.setBackgroundResource(R.drawable.selected_day_background)
        } else if (availableDays.contains(daysOfMonth[position])) {
            textView.setBackgroundResource(R.drawable.selected_time_background)
        } else {
            textView.setBackgroundResource(0)
        }

        return textView
    }
}
