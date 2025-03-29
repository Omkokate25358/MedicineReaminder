package com.example.medicare.Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicare.Fragment.Intake_Fragment;
import com.example.medicare.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private List<Calendar> days;
    private Context context;
    private Intake_Fragment.OnDateSelectedListener listener;
    private int selectedPosition = 0; // Default first item selected

    // Constructor that takes the listener interface as a parameter
    public CalendarAdapter(List<Calendar> days, Context context, Intake_Fragment.OnDateSelectedListener listener) {
        this.days = days;
        this.context = context;
        this.listener = listener;

        // Find and set the position of the current day
        selectedPosition = findCurrentDayPosition();
    }

    // Method to find the position of the current day
    private int findCurrentDayPosition() {
        Calendar today = Calendar.getInstance();
        for (int i = 0; i < days.size(); i++) {
            Calendar day = days.get(i);
            if (isSameDay(today, day)) {
                return i;
            }
        }
        return 0; // Default to first day if no match
    }

    // Helper method to check if two calendars represent the same day
    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        Calendar day = days.get(position);

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.getDefault());

        String dayName = dayFormat.format(day.getTime());
        String date = dateFormat.format(day.getTime());

        holder.tvDayOfWeek.setText(dayName);
        holder.tvDate.setText(date);

        // Check if this is the current day
        Calendar today = Calendar.getInstance();
        boolean isCurrentDay = isSameDay(today, day);

        // Check if this is the selected day
        boolean isSelectedDay = (position == selectedPosition);

        // Set colors based on day type
        if (isSelectedDay) {
            // Selected day gets blue background
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.blue));
            holder.tvDayOfWeek.setTextColor(context.getResources().getColor(R.color.white));
            holder.tvDate.setTextColor(context.getResources().getColor(R.color.white));
        } else if (isCurrentDay) {
            // Current day gets yellow background
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.yellow));
            holder.tvDayOfWeek.setTextColor(context.getResources().getColor(R.color.black));
            holder.tvDate.setTextColor(context.getResources().getColor(R.color.black));
        } else {
            // Normal day gets white background
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));
            holder.tvDayOfWeek.setTextColor(context.getResources().getColor(R.color.black));
            holder.tvDate.setTextColor(context.getResources().getColor(R.color.black));
        }

        // Set click listener
        holder.cardView.setOnClickListener(v -> {
            int oldSelectedPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // Update UI
            notifyItemChanged(oldSelectedPosition);
            notifyItemChanged(selectedPosition);

            // Notify listener
            listener.onDateSelected(day, selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    // Method to update calendar data
    public void updateDates(List<Calendar> newDays) {
        this.days = newDays;
        // Reset selection to today's position in the new week
        selectedPosition = findCurrentDayPosition();
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvDayOfWeek;
        TextView tvDate;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewDay);
            tvDayOfWeek = itemView.findViewById(R.id.tvDayOfWeek);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}