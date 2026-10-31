package com.example.firstaidhero.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "firstaid.db";
    private static final int DB_VERSION = 7;
    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        try {
            copyDatabaseIfNeeded();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error copying from assets, falling back to onCreate", e);
        }
    }

    private void copyDatabaseIfNeeded() {
        File dbFile = context.getDatabasePath(DB_NAME);
        if (!dbFile.exists()) {
            try {
                InputStream inputStream = context.getAssets().open(DB_NAME);
                File parentDir = dbFile.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }
                OutputStream outputStream = new FileOutputStream(dbFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
                Log.d("DatabaseHelper", "Database copied from assets successfully.");
            } catch (IOException e) {
                Log.w("DatabaseHelper", "Assets db not found. SQLiteOpenHelper will create it via onCreate.");
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS emergencies (" +
                "case_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "danger_level TEXT NOT NULL," +
                "symptoms TEXT NOT NULL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS steps (" +
                "step_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "case_id INTEGER," +
                "step_number INTEGER NOT NULL," +
                "instruction_text TEXT NOT NULL," +
                "FOREIGN KEY (case_id) REFERENCES emergencies(case_id) ON DELETE CASCADE);");

        db.execSQL("CREATE TABLE IF NOT EXISTS custom_contacts (" +
                "contact_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "contact_name TEXT NOT NULL," +
                "phone_number TEXT NOT NULL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS treated_cases (" +
                "treated_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "description TEXT NOT NULL," +
                "date TEXT NOT NULL," +
                "photo TEXT," +
                "procedures TEXT NOT NULL);");


        db.execSQL("INSERT INTO emergencies (title, danger_level, symptoms) VALUES ('إسعافات الحروق (الدرجات 1، 2، 3)', 'متدرج', 'تختلف الأعراض حسب الدرجة: احمرار سطحي (درجة 1)، فقاعات مائية (درجة 2)، تفحم وتلف الجلد (درجة 3).');");
        db.execSQL("INSERT INTO emergencies (title, danger_level, symptoms) VALUES ('لدغات الأفاعي والحشرات', 'حرج', 'أثر عضتين، ألم، تورم، صعوبة تنفس، دوخة.');");
        db.execSQL("INSERT INTO emergencies (title, danger_level, symptoms) VALUES ('الاختناق وانسداد مجرى الهواء', 'حرج', 'عدم القدرة على الكلام، الإمساك بالحنجرة، ازرقاق الوجه.');");
        db.execSQL("INSERT INTO emergencies (title, danger_level, symptoms) VALUES ('الكسور والالتواءات', 'متوسط', 'ألم شديد عند الحركة، تشوه في الطرف، تورم وكدمات.');");

        db.execSQL("INSERT INTO steps (case_id, step_number, instruction_text) VALUES (1, 1, '🟢 الدرجة الأولى (البسيطة): تبريد الحرق بماء جارٍ فاتر 10-15 دقيقة، ثم دهن مرهم حروق مرطب وتغطية الحرق بشاش معقم غير لاصق.');");
        db.execSQL("INSERT INTO steps (case_id, step_number, instruction_text) VALUES (1, 2, '🟠 الدرجة الثانية (المتوسطة): تبريد الحرق بماء جارٍ فاتر 15-20 دقيقة، عدم فقع الفقاعات المائية نهائياً لمنع الالتهاب، وتغطيتها بشاش طبي معقم.');");
        db.execSQL("INSERT INTO steps (case_id, step_number, instruction_text) VALUES (1, 3, '🔴 الدرجة الثالثة (الحرجة): الاتصال بطوارئ الإسعاف (123) فوراً، عدم نزع الملابس الملتصقة بالحرق، وتغطية الحرق بضماد جاف معقم لحين وصول الطوارئ.');");

        db.execSQL("INSERT INTO steps (case_id, step_number, instruction_text) VALUES (2, 1, 'إبقاء المصاب هادئاً ومستلقياً لتقليل انتشار السم.');");
        db.execSQL("INSERT INTO steps (case_id, step_number, instruction_text) VALUES (2, 2, 'تثبيت الطرف المصاب في وضعية أقل من مستوى القلب.');");
        db.execSQL("INSERT INTO steps (case_id, step_number, instruction_text) VALUES (2, 3, 'عدم محاولة مص السم أو جرح مكان اللدغة.');");

        db.execSQL("INSERT INTO steps (case_id, step_number, instruction_text) VALUES (3, 1, 'تشجيع المصاب على السعال بقوة.');");
        db.execSQL("INSERT INTO steps (case_id, step_number, instruction_text) VALUES (3, 2, 'تطبيق مناورة هيملك (الضغط البطني) من الخلف إذا استمر الاختناق.');");

        db.execSQL("INSERT INTO steps (case_id, step_number, instruction_text) VALUES (4, 1, 'تثبيت الطرف المصاب بجبيرة مؤقتة وعدم تحريكه.');");
        db.execSQL("INSERT INTO steps (case_id, step_number, instruction_text) VALUES (4, 2, 'وضع كمادات باردة لتخفيف التورم والألم.');");

        db.execSQL("INSERT INTO custom_contacts (contact_name, phone_number) VALUES ('الإسعاف السريع', '123');");
        db.execSQL("INSERT INTO custom_contacts (contact_name, phone_number) VALUES ('الدفاع المدني', '125');");
        db.execSQL("INSERT INTO custom_contacts (contact_name, phone_number) VALUES ('طبيب العائلة (د. أحمد)', '0912345678');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS steps;");
        db.execSQL("DROP TABLE IF EXISTS emergencies;");
        db.execSQL("DROP TABLE IF EXISTS custom_contacts;");
        db.execSQL("DROP TABLE IF EXISTS treated_cases;");
        onCreate(db);
    }
}
