package com.example.medicare.Activity;// CalendarAdapter.java

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicare.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private List<Calendar> dates;
    private Context context;

    public CalendarAdapter(List<Calendar> dates, Context context) {
        this.dates = dates;
        this.context = context;
    }

    // Method to update dates when navigating weeks
    public void updateDates(List<Calendar> newDates) {
        this.dates = newDates;
        notifyDataSetChanged();
    }

    public class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dayNumberTextView;
        TextView dayNameTextView;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dayNumberTextView = itemView.findViewById(R.id.textViewDayNumber);
            dayNameTextView = itemView.findViewById(R.id.textViewDayName);
        }

        public void bind(Calendar calendar) {
            // Format day number
            SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
            dayNumberTextView.setText(dayFormat.format(calendar.getTime()));

            // Format day name (abbreviated)
            SimpleDateFormat dayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
            dayNameTextView.setText(dayNameFormat.format(calendar.getTime()).toUpperCase());

            // Check if this is today's date
            Calendar today = Calendar.getInstance();
            boolean isToday = isSameDay(calendar, today);

            // Set background based on today's date
            if (isToday) {
                itemView.setBackgroundColor(Color.YELLOW);
            } else {
                // Reset to default background
                itemView.setBackground(context.getDrawable(R.drawable.calendar_day_background));
            }
        }

        // Helper method to check if two calendars represent the same day
        private boolean isSameDay(Calendar cal1, Calendar cal2) {
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                    cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
        }
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        holder.bind(dates.get(position));
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }
}