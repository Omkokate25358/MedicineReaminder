package com.example.medicare.Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicare.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private List<Calendar> days;
    private Context context;
    private Intake_Activity.OnDateSelectedListener listener;
    private int selectedPosition = 0; // Default first item selected

    // Constructor that takes the listener interface as a parameter
    public CalendarAdapter(List<Calendar> days, Context context, Intake_Activity.OnDateSelectedListener listener) {
        this.days = days;
        this.context = context;
        this.listener = listener;
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

        // Highlight selected day
        if (position == selectedPosition) {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.blue));
            holder.tvDayOfWeek.setTextColor(context.getResources().getColor(R.color.white));
            holder.tvDate.setTextColor(context.getResources().getColor(R.color.white));
        } else {
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
        selectedPosition = 0; // Reset selection to first item
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