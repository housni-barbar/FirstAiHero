package com.example.firstaidhero.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstaidhero.R;
import com.example.firstaidhero.models.TreatedCase;

import java.util.List;

public class TreatedCaseAdapter extends RecyclerView.Adapter<TreatedCaseAdapter.ViewHolder> {
    private final List<TreatedCase> treatedCaseList;
    private final OnTreatedCaseActionListener listener;

    public interface OnTreatedCaseActionListener {
        void onEditClick(TreatedCase treatedCase);
        void onDeleteClick(TreatedCase treatedCase);
    }

    public TreatedCaseAdapter(List<TreatedCase> treatedCaseList, OnTreatedCaseActionListener listener) {
        this.treatedCaseList = treatedCaseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_treated_case, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TreatedCase treatedCase = treatedCaseList.get(position);
        holder.tvDate.setText("التاريخ: " + treatedCase.getDate());
        holder.tvDesc.setText(treatedCase.getDescription());
        holder.tvProc.setText("الإجراءات: " + treatedCase.getProcedures());

        if (treatedCase.getPhoto() != null && !treatedCase.getPhoto().isEmpty()) {
            try {
                holder.ivPhoto.setImageURI(Uri.parse(treatedCase.getPhoto()));
                holder.ivPhoto.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                holder.ivPhoto.setVisibility(View.GONE);
            }
        } else {
            holder.ivPhoto.setVisibility(View.GONE);
        }

        if (holder.btnEdit != null) {
            holder.btnEdit.setOnClickListener(v -> listener.onEditClick(treatedCase));
        }
        if (holder.btnDelete != null) {
            holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(treatedCase));
        }
    }

    @Override
    public int getItemCount() {
        return treatedCaseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvDesc, tvProc;
        ImageView ivPhoto;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvTreatedDate);
            tvDesc = itemView.findViewById(R.id.tvTreatedDescription);
            tvProc = itemView.findViewById(R.id.tvTreatedProcedures);
            ivPhoto = itemView.findViewById(R.id.ivTreatedPhoto);
            btnEdit = itemView.findViewById(R.id.btnEditTreated);
            btnDelete = itemView.findViewById(R.id.btnDeleteTreated);
        }
    }
}
