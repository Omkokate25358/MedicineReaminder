package com.example.medicare.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicare.Activity.Edit_Medicine_Activity;
import com.example.medicare.Model.MedicineModel;
import com.example.medicare.R;

import java.util.ArrayList;
import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    private List<MedicineModel> medicineList;
    private Context context;

    // Add these fields to store document IDs and weekDay
    private List<String> documentIds;
    private String weekDay;

    public MedicineAdapter(Context context, List<MedicineModel> medicineList, String weekDay) {
        this.context = context;
        this.medicineList = medicineList;
        this.documentIds = new ArrayList<>();
        this.weekDay = weekDay;
    }

    // Add method to update data
    public void updateData(List<MedicineModel> newMedicineList, List<String> newDocumentIds) {
        this.medicineList = newMedicineList;
        this.documentIds = newDocumentIds;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        // Bind data to the view holder
    }

    @Override
    public int getItemCount() {
        return 0; // Implement this based on your data structure
    }

    public class MedicineViewHolder extends RecyclerView.ViewHolder {
        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // Get the document ID for the clicked item
                    String documentId = documentIds.get(position);

                    // Start Edit Activity
                    Intent intent = new Intent(context, Edit_Medicine_Activity.class);
                    intent.putExtra("documentId", documentId);
                    intent.putExtra("weekDay", weekDay);
                    context.startActivity(intent);
                }
            });
        }
    }
} 