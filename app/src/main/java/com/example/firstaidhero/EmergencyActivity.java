package com.example.firstaidhero;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstaidhero.adapters.ContactAdapter;
import com.example.firstaidhero.database.DatabaseHelper;
import com.example.firstaidhero.models.EmergencyContact;

import java.util.ArrayList;
import java.util.List;

public class EmergencyActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private List<EmergencyContact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerViewContacts);
        Button btnAddContact = findViewById(R.id.btnAddContact);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadContacts();

        btnAddContact.setOnClickListener(v -> showAddContactDialog());
    }

    private void loadContacts() {
        contactList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM custom_contacts", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("contact_id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("contact_name"));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone_number"));
                contactList.add(new EmergencyContact(id, name, phone));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        adapter = new ContactAdapter(contactList, new ContactAdapter.OnContactActionListener() {
            @Override
            public void onCallClick(String phoneNumber) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            }

            @Override
            public void onEditClick(EmergencyContact contact) {
                showEditContactDialog(contact);
            }

            @Override
            public void onDeleteClick(EmergencyContact contact) {
                showDeleteConfirmationDialog(contact);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void showEditContactDialog(EmergencyContact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("تعديل رقم الطوارئ");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        final EditText inputName = new EditText(this);
        inputName.setHint("اسم الجهة");
        inputName.setText(contact.getContactName());
        layout.addView(inputName);

        final EditText inputPhone = new EditText(this);
        inputPhone.setHint("رقم الهاتف");
        inputPhone.setText(contact.getPhoneNumber());
        inputPhone.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        layout.addView(inputPhone);

        builder.setView(layout);

        builder.setPositiveButton("تحديث", (dialog, which) -> {
            String name = inputName.getText().toString().trim();
            String phone = inputPhone.getText().toString().trim();
            if (!name.isEmpty() && !phone.isEmpty()) {
                updateContact(contact.getContactId(), name, phone);
                loadContacts();
                Toast.makeText(this, "تم التعديل بنجاح", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "الرجاء ملء الحقول", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("إلغاء", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showDeleteConfirmationDialog(EmergencyContact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("حذف جهة الاتصال");
        builder.setMessage("هل أنت تأكد من حذف " + contact.getContactName() + "؟");

        builder.setPositiveButton("حذف", (dialog, which) -> {
            deleteContact(contact.getContactId());
            loadContacts();
            Toast.makeText(this, "تم الحذف بنجاح", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("إلغاء", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void updateContact(int contactId, String name, String phone) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("contact_name", name);
        values.put("phone_number", phone);
        db.update("custom_contacts", values, "contact_id = ?", new String[]{String.valueOf(contactId)});
        db.close();
    }

    private void deleteContact(int contactId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("custom_contacts", "contact_id = ?", new String[]{String.valueOf(contactId)});
        db.close();
    }

    private void showAddContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("إضافة رقم طوارئ جديد");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        final EditText inputName = new EditText(this);
        inputName.setHint("اسم الجهة (مثل: طبيب العائلة)");
        layout.addView(inputName);

        final EditText inputPhone = new EditText(this);
        inputPhone.setHint("رقم الهاتف");
        inputPhone.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        layout.addView(inputPhone);

        builder.setView(layout);

        builder.setPositiveButton("حفظ", (dialog, which) -> {
            String name = inputName.getText().toString().trim();
            String phone = inputPhone.getText().toString().trim();
            if (!name.isEmpty() && !phone.isEmpty()) {
                insertContact(name, phone);
                loadContacts();
                Toast.makeText(this, "تم بنجاح", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "الرجاء ملء الحقول", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("إلغاء", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void insertContact(String name, String phone) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("contact_name", name);
        values.put("phone_number", phone);
        db.insert("custom_contacts", null, values);
        db.close();
    }
}
