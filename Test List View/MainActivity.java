app -> kotlin+java -> com.example.testkalkulator -> MainActivity.java

package com.example.testlistview;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    EditText etNrp, etNama;
    Button btnSimpan, btnCari, btnUpdate, btnHapus, btnLihatData;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "onCreate: Aplikasi Dibuka", Toast.LENGTH_SHORT).show();

        // Inisialisasi Database
        dbHelper = new DatabaseHelper(this);

        // Hubungkan variabel dengan komponen di XML
        etNrp = findViewById(R.id.etNrp);
        etNama = findViewById(R.id.etNama);
        btnSimpan = findViewById(R.id.btnSimpan);
        btnCari = findViewById(R.id.btnCari);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnHapus = findViewById(R.id.btnHapus);
        btnLihatData = findViewById(R.id.btnLihatData);

        // --- AKSI TOMBOL SIMPAN (CREATE) ---
        btnSimpan.setOnClickListener(v -> {
            String nrp = etNrp.getText().toString();
            String nama = etNama.getText().toString();

            if (nrp.isEmpty() || nama.isEmpty()) {
                Toast.makeText(this, "Isi semua data!", Toast.LENGTH_SHORT).show();
            } else {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COL_NRP, nrp);
                values.put(DatabaseHelper.COL_NAMA, nama);

                long hasil = db.insert(DatabaseHelper.TABLE_NAME, null, values);
                if (hasil != -1) {
                    Toast.makeText(this, "Data Tersimpan!", Toast.LENGTH_SHORT).show();
                    etNrp.setText("");
                    etNama.setText("");
                } else {
                    Toast.makeText(this, "Gagal! NRP mungkin sudah ada.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // --- AKSI TOMBOL CARI (READ) ---
        btnCari.setOnClickListener(v -> {
            String nrp = etNrp.getText().toString();
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            // Cari berdasarkan NRP
            Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME + " WHERE " + DatabaseHelper.COL_NRP + "=?", new String[]{nrp});

            if (cursor.moveToFirst()) {
                etNama.setText(cursor.getString(1)); // Ambil nama dari kolom index 1
                Toast.makeText(this, "Data Ditemukan", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Data Tidak Ditemukan", Toast.LENGTH_SHORT).show();
                etNama.setText("");
            }
            cursor.close();
        });

        // --- AKSI TOMBOL UPDATE ---
        btnUpdate.setOnClickListener(v -> {
            String nrp = etNrp.getText().toString();
            String nama = etNama.getText().toString();

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_NAMA, nama);

            // Update nama berdasarkan NRP
            int hasil = db.update(DatabaseHelper.TABLE_NAME, values, DatabaseHelper.COL_NRP + "=?", new String[]{nrp});
            if (hasil > 0) {
                Toast.makeText(this, "Data Berhasil Diupdate", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Gagal Update, NRP tidak ditemukan", Toast.LENGTH_SHORT).show();
            }
        });

        // --- AKSI TOMBOL HAPUS (DELETE) ---
        btnHapus.setOnClickListener(v -> {
            String nrp = etNrp.getText().toString();
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            int hasil = db.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COL_NRP + "=?", new String[]{nrp});
            if (hasil > 0) {
                Toast.makeText(this, "Data Terhapus", Toast.LENGTH_SHORT).show();
                etNrp.setText("");
                etNama.setText("");
            } else {
                Toast.makeText(this, "Gagal Hapus, NRP tidak ditemukan", Toast.LENGTH_SHORT).show();
            }
        });

        // --- AKSI TOMBOL LIHAT DATA (PINDAH HALAMAN) ---
        btnLihatData.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            startActivity(intent);
        });
    }

    // --- IMPLEMENTASI LIFECYCLE ACTIVITY ---
    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, "onStart: Aplikasi Terlihat", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Lifecycle", "onResume: Aplikasi Aktif");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Lifecycle", "onPause: Aplikasi Dijeda");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(this, "onStop: Aplikasi Berhenti", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
