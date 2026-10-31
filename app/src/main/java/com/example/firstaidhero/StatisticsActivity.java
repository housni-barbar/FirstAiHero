package com.example.firstaidhero;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firstaidhero.database.DatabaseHelper;

public class StatisticsActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        dbHelper = new DatabaseHelper(this);

        TextView tvCritical = findViewById(R.id.tvCriticalCount);
        TextView tvMedium = findViewById(R.id.tvMediumCount);
        TextView tvLow = findViewById(R.id.tvLowCount);
        TextView tvTreated = findViewById(R.id.tvTreatedCount);

        loadStatistics(tvCritical, tvMedium, tvLow, tvTreated);
    }

    private void loadStatistics(TextView tvCritical, TextView tvMedium, TextView tvLow, TextView tvTreated) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        int criticalCount = getCountByDanger(db, "حرج");
        int mediumCount = getCountByDanger(db, "متوسط");
        int lowCount = getCountByDanger(db, "طفيف");
        int treatedCount = getTableCount(db, "treated_cases");

        db.close();

        tvCritical.setText(String.valueOf(criticalCount));
        tvMedium.setText(String.valueOf(mediumCount));
        tvLow.setText(String.valueOf(lowCount));
        tvTreated.setText(String.valueOf(treatedCount));
    }

    private int getCountByDanger(SQLiteDatabase db, String dangerLevel) {
        int count = 0;

        Cursor cursor1 = db.rawQuery("SELECT COUNT(*) FROM emergencies WHERE danger_level = ?", new String[]{dangerLevel});
        if (cursor1.moveToFirst()) {
            count += cursor1.getInt(0);
        }
        cursor1.close();

        Cursor cursor2 = db.rawQuery("SELECT COUNT(*) FROM treated_cases WHERE description LIKE ?", new String[]{"%" + dangerLevel + "%"});
        if (cursor2.moveToFirst()) {
            count += cursor2.getInt(0);
        }
        cursor2.close();

        return count;
    }

    private int getTableCount(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
}
