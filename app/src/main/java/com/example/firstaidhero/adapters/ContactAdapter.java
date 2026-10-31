package com.example.firstaidhero.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstaidhero.R;
import com.example.firstaidhero.models.EmergencyContact;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private final List<EmergencyContact> contactList;
    private final OnContactActionListener listener;

    public interface OnContactActionListener {
        void onCallClick(String phoneNumber);
        void onEditClick(EmergencyContact contact);
        void onDeleteClick(EmergencyContact contact);
    }

    public ContactAdapter(List<EmergencyContact> contactList, OnContactActionListener listener) {
        this.contactList = contactList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmergencyContact contact = contactList.get(position);
        holder.tvName.setText(contact.getContactName());
        holder.tvPhone.setText(contact.getPhoneNumber());

        holder.btnCall.setOnClickListener(v -> listener.onCallClick(contact.getPhoneNumber()));

        // Official default emergency numbers (e.g. 123, 125, or initial system contacts) cannot be edited or deleted
        boolean isOfficialNumber = contact.getPhoneNumber().equals("123") || 
                                   contact.getPhoneNumber().equals("125") || 
                                   contact.getContactId() <= 2;

        if (isOfficialNumber) {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        } else {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> listener.onEditClick(contact));
            holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(contact));
        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone;
        Button btnCall, btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvContactName);
            tvPhone = itemView.findViewById(R.id.tvPhoneNumber);
            btnCall = itemView.findViewById(R.id.btnCall);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
