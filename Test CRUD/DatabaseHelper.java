app -> kotlin+java -> com.example.testkalkulator -> DatabaseHelper.java

package com.example.testcrud;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    // Konfigurasi Database
    public static final String DATABASE_NAME = "mahasiswa.db";
    public static final int DATABASE_VERSION = 1;

    // Konfigurasi Tabel
    public static final String TABLE_NAME = "mahasiswa";
    public static final String COL_ID = "id";
    public static final String COL_NRP = "nrp";
    public static final String COL_NAMA = "nama";

    // Query Create Table
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NRP + " TEXT NOT NULL, " +
                    COL_NAMA + " TEXT NOT NULL);";

    private static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "DatabaseHelper: constructor dipanggil");
    }

    // Dipanggil pertama kali database dibuat
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: membuat tabel " + TABLE_NAME);
        db.execSQL(SQL_CREATE_TABLE);
    }

    // Dipanggil ketika DATABASE_VERSION dinaikkan
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: dari versi " + oldVersion + " ke " + newVersion);
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }

    // ==================== OPERASI CRUD ====================

    // CREATE - Pake NRP+NAMA
    public long simpanData(String nrp, String nama) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NRP, nrp);
        values.put(COL_NAMA, nama);

        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    // READ - Pake NRP
    public Cursor ambilDataByNrp(String nrp) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_NRP + " = ?",
                new String[]{nrp}
        );
    }

    // READ - Pake Semua Data
    public Cursor ambilSemuaData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    // UPDATE - Pake NRP
    public int updateData(String nrp, String namaBaru) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAMA, namaBaru);

        int rows = db.update(
                TABLE_NAME,
                values,
                COL_NRP + " = ?",
                new String[]{nrp}
        );
        db.close();
        return rows;
    }

    // DELETE - Pake NRP
    public int hapusData(String nrp) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(
                TABLE_NAME,
                COL_NRP + " = ?",
                new String[]{nrp}
        );
        db.close();
        return rows;
    }
}
