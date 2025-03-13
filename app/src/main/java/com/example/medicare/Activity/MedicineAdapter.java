package com.example.medicare.Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicare.R;

import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {
    private Context context;
    private List<Medicine> medicineList;
    private OnEditClickListener editClickListener;

    public interface OnEditClickListener {
        void onEditClick(Medicine medicine);
    }

    public MedicineAdapter(Context context, List<Medicine> medicineList, OnEditClickListener listener) {
        this.context = context;
        this.medicineList = medicineList;
        this.editClickListener = listener;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);
        holder.tvName.setText(medicine.getName());
        holder.tvDose.setText("Dose: " + medicine.getDose());
        holder.tvQuantity.setText("Quantity: " + medicine.getQuantity());
        holder.tvTime.setText("Time: " + medicine.getTime());

        holder.btnEdit.setOnClickListener(v -> editClickListener.onEditClick(medicine));
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDose, tvQuantity, tvTime;
        Button btnEdit;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDose = itemView.findViewById(R.id.tvDose);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
