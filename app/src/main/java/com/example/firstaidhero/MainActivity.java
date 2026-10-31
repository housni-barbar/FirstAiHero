package com.example.firstaidhero;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstaidhero.adapters.EmergencyAdapter;
import com.example.firstaidhero.database.DatabaseHelper;
import com.example.firstaidhero.models.Emergency;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private EmergencyAdapter adapter;
    private List<Emergency> emergencyList;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerViewEmergencies);
        etSearch = findViewById(R.id.etSearch);
        Button btnEmergencyContacts = findViewById(R.id.btnEmergencyContacts);
        Button btnTreatedCases = findViewById(R.id.btnTreatedCases);
        Button btnStatistics = findViewById(R.id.btnStatistics);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emergencyList = loadEmergenciesFromDB("");

        adapter = new EmergencyAdapter(emergencyList, new EmergencyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Emergency emergency) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("emergency_id", emergency.getCaseId());
                intent.putExtra("emergency_title", emergency.getTitle());
                intent.putExtra("emergency_danger", emergency.getDangerLevel());
                intent.putExtra("emergency_symptoms", emergency.getSymptoms());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Emergency emergency) {
                showDeleteEmergencyDialog(emergency);
            }
        });

        recyclerView.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emergencyList = loadEmergenciesFromDB(s.toString());
                adapter.updateList(emergencyList);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnEmergencyContacts.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, EmergencyActivity.class)));
        btnTreatedCases.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TreatedCasesActivity.class)));
        btnStatistics.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, StatisticsActivity.class)));
    }

    private void showDeleteEmergencyDialog(Emergency emergency) {
        new AlertDialog.Builder(this)
            .setTitle("حذف الحالة الإسعافية")
            .setMessage("هل أنت تأكد من حذف " + emergency.getTitle() + "؟")
            .setPositiveButton("حذف", (dialog, which) -> {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete("emergencies", "case_id = ?", new String[]{String.valueOf(emergency.getCaseId())});
                db.close();
                emergencyList = loadEmergenciesFromDB(etSearch.getText().toString());
                adapter.updateList(emergencyList);
            })
            .setNegativeButton("إلغاء", null)
            .show();
    }

    private List<Emergency> loadEmergenciesFromDB(String query) {
        List<Emergency> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        if (query.isEmpty()) {
            cursor = db.rawQuery("SELECT * FROM emergencies", null);
        } else {
            cursor = db.rawQuery("SELECT * FROM emergencies WHERE title LIKE ? OR symptoms LIKE ?", 
                    new String[]{"%" + query + "%", "%" + query + "%"});
        }

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("case_id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String danger = cursor.getString(cursor.getColumnIndexOrThrow("danger_level"));
                String symptoms = cursor.getString(cursor.getColumnIndexOrThrow("symptoms"));
                list.add(new Emergency(id, title, danger, symptoms));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
}
