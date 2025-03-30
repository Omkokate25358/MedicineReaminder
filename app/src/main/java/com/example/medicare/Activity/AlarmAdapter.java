package com.example.medicare.Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicare.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {
    private Context context;
    private List<Medicine> medicineList;
    private Map<String, List<String>> medicineDaysMap;

    public AlarmAdapter(Context context, List<Medicine> medicineList) {
        this.context = context;
        this.medicineList = medicineList;
        this.medicineDaysMap = new HashMap<>();
        groupMedicinesByDays();
    }

    private void groupMedicinesByDays() {
        medicineDaysMap.clear();
        for (Medicine medicine : medicineList) {
            String medicineName = medicine.getName();
            String day = medicine.getMedicineDay();
            
            if (!medicineDaysMap.containsKey(medicineName)) {
                medicineDaysMap.put(medicineName, new ArrayList<>());
            }
            medicineDaysMap.get(medicineName).add(day);
        }
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);
        String medicineName = medicine.getName();
        List<String> days = medicineDaysMap.get(medicineName);
        
        holder.tvMedicineName.setText(medicineName);
        
        // Format days as a comma-separated string
        String daysText = String.join(", ", days);
        holder.tvMedicineDay.setText("Days: " + daysText);
        
        holder.tvMedicineDose.setText("Dose: " + medicine.getDose());
        holder.tvMedicineQuantity.setText("Quantity: " + medicine.getQuantity());
        holder.tvMedicineTime.setText("Time: " + medicine.getTime());
    }

    @Override
    public int getItemCount() {
        return medicineDaysMap.size();
    }

    static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedicineName, tvMedicineDay, tvMedicineDose, tvMedicineQuantity, tvMedicineTime;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedicineName = itemView.findViewById(R.id.tvMedicineName);
            tvMedicineDay = itemView.findViewById(R.id.tvMedicineDay);
            tvMedicineDose = itemView.findViewById(R.id.tvMedicineDose);
            tvMedicineQuantity = itemView.findViewById(R.id.tvMedicineQuantity);
            tvMedicineTime = itemView.findViewById(R.id.tvMedicineTime);
        }
    }
} 