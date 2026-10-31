package com.example.firstaidhero.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstaidhero.R;
import com.example.firstaidhero.models.Emergency;

import java.util.List;

public class EmergencyAdapter extends RecyclerView.Adapter<EmergencyAdapter.ViewHolder> {
    private List<Emergency> emergencyList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Emergency emergency);
        void onItemLongClick(Emergency emergency);
    }

    public EmergencyAdapter(List<Emergency> emergencyList, OnItemClickListener listener) {
        this.emergencyList = emergencyList;
        this.listener = listener;
    }

    public void updateList(List<Emergency> newList) {
        this.emergencyList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emergency, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Emergency emergency = emergencyList.get(position);
        holder.tvTitle.setText(emergency.getTitle());
        holder.tvDanger.setText(emergency.getDangerLevel());
        holder.tvSymptoms.setText(emergency.getSymptoms());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(emergency));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(emergency);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return emergencyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDanger, tvSymptoms;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvEmergencyTitle);
            tvDanger = itemView.findViewById(R.id.tvDangerLevel);
            tvSymptoms = itemView.findViewById(R.id.tvEmergencySymptoms);
        }
    }
}
