package com.example.firstaidhero;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstaidhero.adapters.TreatedCaseAdapter;
import com.example.firstaidhero.database.DatabaseHelper;
import com.example.firstaidhero.models.TreatedCase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TreatedCasesActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private TreatedCaseAdapter adapter;
    private List<TreatedCase> caseList;
    private EditText etSearchTreated;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private String currentSelectedPhotoUri = "";
    private ImageView dialogImagePreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treated_cases);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerViewTreatedCases);
        etSearchTreated = findViewById(R.id.etSearchTreated);
        Button btnAddCase = findViewById(R.id.btnAddTreatedCase);

        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    Uri selectedUri = result.getData().getData();
                    try {
                        getContentResolver().takePersistableUriPermission(selectedUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (Exception ignored) {}
                    currentSelectedPhotoUri = selectedUri.toString();
                    if (dialogImagePreview != null) {
                        dialogImagePreview.setImageURI(selectedUri);
                        dialogImagePreview.setVisibility(View.VISIBLE);
                    }
                }
            }
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadTreatedCases("");

        etSearchTreated.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadTreatedCases(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnAddCase.setOnClickListener(v -> showAddCaseDialog());
    }

    private void loadTreatedCases(String query) {
        caseList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        if (query.isEmpty()) {
            cursor = db.rawQuery("SELECT * FROM treated_cases ORDER BY date DESC, treated_id DESC", null);
        } else {
            cursor = db.rawQuery("SELECT * FROM treated_cases WHERE description LIKE ? OR procedures LIKE ? OR date LIKE ? ORDER BY date DESC, treated_id DESC", 
                    new String[]{"%" + query + "%", "%" + query + "%", "%" + query + "%"});
        }

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("treated_id"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String photo = cursor.getString(cursor.getColumnIndexOrThrow("photo"));
                String procedures = cursor.getString(cursor.getColumnIndexOrThrow("procedures"));
                caseList.add(new TreatedCase(id, desc, date, photo, procedures));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        adapter = new TreatedCaseAdapter(caseList, new TreatedCaseAdapter.OnTreatedCaseActionListener() {
            @Override
            public void onEditClick(TreatedCase treatedCase) {
                showEditTreatedCaseDialog(treatedCase);
            }

            @Override
            public void onDeleteClick(TreatedCase treatedCase) {
                showDeleteTreatedCaseDialog(treatedCase);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private String getCurrentQuery() {
        return (etSearchTreated != null) ? etSearchTreated.getText().toString() : "";
    }

    private void showEditTreatedCaseDialog(TreatedCase treatedCase) {
        currentSelectedPhotoUri = treatedCase.getPhoto() != null ? treatedCase.getPhoto() : "";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("تعديل الحالة المعالجة");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        final EditText inputDesc = new EditText(this);
        inputDesc.setHint("وصف الحالة");
        inputDesc.setText(treatedCase.getDescription());
        layout.addView(inputDesc);

        final EditText inputProc = new EditText(this);
        inputProc.setHint("الإجراءات التي تمت");
        inputProc.setText(treatedCase.getProcedures());
        layout.addView(inputProc);

        Button btnPickImage = new Button(this);
        btnPickImage.setText("تعديل/إرفاق صورة جديدة");
        btnPickImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });
        layout.addView(btnPickImage);

        dialogImagePreview = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 250);
        params.setMargins(0, 16, 0, 16);
        dialogImagePreview.setLayoutParams(params);
        dialogImagePreview.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if (!currentSelectedPhotoUri.isEmpty()) {
            try {
                dialogImagePreview.setImageURI(Uri.parse(currentSelectedPhotoUri));
                dialogImagePreview.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                dialogImagePreview.setVisibility(View.GONE);
            }
        } else {
            dialogImagePreview.setVisibility(View.GONE);
        }
        layout.addView(dialogImagePreview);

        builder.setView(layout);

        builder.setPositiveButton("تحديث", (dialog, which) -> {
            String desc = inputDesc.getText().toString().trim();
            String proc = inputProc.getText().toString().trim();

            if (!desc.isEmpty() && !proc.isEmpty()) {
                updateTreatedCase(treatedCase.getTreatedId(), desc, currentSelectedPhotoUri, proc);
                loadTreatedCases(getCurrentQuery());
                Toast.makeText(this, "تم التعديل بنجاح", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "الرجاء ملء الحقول المطلوبة", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("إلغاء", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showDeleteTreatedCaseDialog(TreatedCase treatedCase) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("حذف الحالة المعالجة");
        builder.setMessage("هل أنت تأكد من حذف هذه الحالة المعالجة؟");

        builder.setPositiveButton("حذف", (dialog, which) -> {
            deleteTreatedCase(treatedCase.getTreatedId());
            loadTreatedCases(getCurrentQuery());
            Toast.makeText(this, "تم الحذف بنجاح", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("إلغاء", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void updateTreatedCase(int id, String desc, String photo, String procedures) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("description", desc);
        values.put("photo", photo);
        values.put("procedures", procedures);
        db.update("treated_cases", values, "treated_id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    private void deleteTreatedCase(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("treated_cases", "treated_id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    private void showAddCaseDialog() {
        currentSelectedPhotoUri = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("إدخال حالة معالجة جديدة");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        final EditText inputDesc = new EditText(this);
        inputDesc.setHint("وصف الحالة (مثال: لدغة أفعى)");
        layout.addView(inputDesc);

        TextView tvSpinnerLabel = new TextView(this);
        tvSpinnerLabel.setText("نوع/درجة الخطورة:");
        tvSpinnerLabel.setTextSize(16);
        tvSpinnerLabel.setPadding(0, 16, 0, 8);
        layout.addView(tvSpinnerLabel);

        final Spinner spinnerDanger = new Spinner(this);
        String[] levels = new String[]{"حرج", "متوسط", "طفيف"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, levels);
        spinnerDanger.setAdapter(spinnerAdapter);
        layout.addView(spinnerDanger);

        final EditText inputProc = new EditText(this);
        inputProc.setHint("الإجراءات التي تمت");
        layout.addView(inputProc);

        Button btnPickImage = new Button(this);
        btnPickImage.setText("إرفاق صورة للحالة");
        btnPickImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });
        layout.addView(btnPickImage);

        dialogImagePreview = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 250);
        params.setMargins(0, 16, 0, 16);
        dialogImagePreview.setLayoutParams(params);
        dialogImagePreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        dialogImagePreview.setVisibility(View.GONE);
        layout.addView(dialogImagePreview);

        builder.setView(layout);

        builder.setPositiveButton("حفظ", (dialog, which) -> {
            String desc = inputDesc.getText().toString().trim();
            String proc = inputProc.getText().toString().trim();
            String selectedLevel = spinnerDanger.getSelectedItem().toString();

            if (!desc.isEmpty() && !proc.isEmpty()) {
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                String fullDesc = desc + " [الخطورة: " + selectedLevel + "]";
                insertTreatedCase(fullDesc, currentDate, currentSelectedPhotoUri, proc);
                loadTreatedCases(getCurrentQuery());
                Toast.makeText(this, "تم الحفظ بنجاح", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "الرجاء ملء الحقول المطلوبة", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("إلغاء", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void insertTreatedCase(String desc, String date, String photo, String procedures) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        // Insert into treated_cases table only
        ContentValues values = new ContentValues();
        values.put("description", desc);
        values.put("date", date);
        values.put("photo", photo);
        values.put("procedures", procedures);
        db.insert("treated_cases", null, values);

        db.close();
    }
}
