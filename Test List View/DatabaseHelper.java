app -> kotlin+java -> com.example.testkalkulator -> DatabaseHelper.java

package com.example.testlistview;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mahasiswa.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "mahasiswa";
    public static final String COL_NRP = "nrp";
    public static final String COL_NAMA = "nama";

    // Query untuk membuat tabel
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COL_NRP + " TEXT PRIMARY KEY, " +
            COL_NAMA + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Dibuat saat database pertama kali dibuat
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Dijalankan jika versi database dinaikkan
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
