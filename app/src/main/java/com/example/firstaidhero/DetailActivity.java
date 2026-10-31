package com.example.firstaidhero;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstaidhero.adapters.StepAdapter;
import com.example.firstaidhero.database.DatabaseHelper;
import com.example.firstaidhero.models.EmergencyStep;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dbHelper = new DatabaseHelper(this);

        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvDanger = findViewById(R.id.tvDetailDanger);
        TextView tvSymptoms = findViewById(R.id.tvDetailSymptoms);
        RecyclerView recyclerViewSteps = findViewById(R.id.recyclerViewSteps);
        Button btnCallQuick = findViewById(R.id.btnCallEmergencyQuick);

        int caseId = getIntent().getIntExtra("emergency_id", 1);
        String title = getIntent().getStringExtra("emergency_title");
        String danger = getIntent().getStringExtra("emergency_danger");
        String symptoms = getIntent().getStringExtra("emergency_symptoms");

        tvTitle.setText(title);
        tvDanger.setText("مستوى الخطورة: " + danger);
        tvSymptoms.setText("الأعراض: " + symptoms);

        recyclerViewSteps.setLayoutManager(new LinearLayoutManager(this));
        List<EmergencyStep> stepList = loadStepsForCase(caseId);
        StepAdapter adapter = new StepAdapter(stepList);
        recyclerViewSteps.setAdapter(adapter);

        btnCallQuick.setOnClickListener(v -> showEmergencyChoiceDialog());
    }

    private void showEmergencyChoiceDialog() {
        String[] options = new String[]{"🚑 الإسعاف السريع (123)", "🚒 الدفاع المدني (125)"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("اختر جهة الاتصال الطارئة");
        builder.setItems(options, (dialog, which) -> {
            String phoneNumber = (which == 0) ? "123" : "125";
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        });
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }

    private List<EmergencyStep> loadStepsForCase(int caseId) {
        List<EmergencyStep> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM steps WHERE case_id = ? ORDER BY step_number ASC", new String[]{String.valueOf(caseId)});

        if (cursor.moveToFirst()) {
            do {
                int stepId = cursor.getInt(cursor.getColumnIndexOrThrow("step_id"));
                int cId = cursor.getInt(cursor.getColumnIndexOrThrow("case_id"));
                int stepNum = cursor.getInt(cursor.getColumnIndexOrThrow("step_number"));
                String instruction = cursor.getString(cursor.getColumnIndexOrThrow("instruction_text"));
                list.add(new EmergencyStep(stepId, cId, stepNum, instruction));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
}
