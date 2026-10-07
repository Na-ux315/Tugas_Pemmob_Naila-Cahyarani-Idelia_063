app -> kotlin+java -> com.example.testkalkulator -> MainActivity.java

package com.example.testcrud;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityLifecycle";

    private EditText etNrp, etNama;
    private TextView tvHasil;
    private DatabaseHelper dbHelper;

    // ==================== LIFECYCLE METHODS ====================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Activity dibuat");

        etNrp = findViewById(R.id.etNrp);
        etNama = findViewById(R.id.etNama);
        tvHasil = findViewById(R.id.tvHasil);
        Button btnSimpan = findViewById(R.id.btnSimpan);
        Button btnAmbil = findViewById(R.id.btnAmbil);
        Button btnUpdate = findViewById(R.id.btnUpdate);
        Button btnHapus = findViewById(R.id.btnHapus);

        // Inisialisasi Database Helper
        dbHelper = new DatabaseHelper(this);

        // Tombol SIMPAN
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpanData();
            }
        });

        // Tombol AMBIL DATA
        btnAmbil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ambilData();
            }
        });

        // Tombol UPDATE
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });

        // Tombol HAPUS
        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hapusData();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Activity akan tampil");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Activity aktif & interaktif");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Activity dijeda");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Activity berhenti / tidak terlihat");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: Activity akan dimulai ulang");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity dihancurkan");
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    // ==================== METHOD CRUD ====================

    // SIMPAN
    private void simpanData() {
        String nrp = etNrp.getText().toString().trim();
        String nama = etNama.getText().toString().trim();

        if (nrp.isEmpty() || nama.isEmpty()) {
            Toast.makeText(this, "NRP dan Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        long id = dbHelper.simpanData(nrp, nama);
        if (id > 0) {
            Toast.makeText(this, "Data berhasil disimpan (ID: " + id + ")", Toast.LENGTH_SHORT).show();
            tvHasil.setText("✔ Data tersimpan:\nNRP: " + nrp + "\nNama: " + nama);
            kosongkanField();
        } else {
            Toast.makeText(this, "Gagal menyimpan data!", Toast.LENGTH_SHORT).show();
        }
    }

    // AMBIL DATA
    private void ambilData() {
        String nrp = etNrp.getText().toString().trim();

        Cursor cursor;
        if (nrp.isEmpty()) {
            // Jika NRP kosong, tampilkan semua data
            cursor = dbHelper.ambilSemuaData();
        } else {
            cursor = dbHelper.ambilDataByNrp(nrp);
        }

        if (cursor == null || cursor.getCount() == 0) {
            tvHasil.setText("Tidak ada data ditemukan.");
            Toast.makeText(this, "Data kosong", Toast.LENGTH_SHORT).show();
            if (cursor != null) cursor.close();
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== DAFTAR MAHASISWA ===\n\n");

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
            String n = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NRP));
            String nm = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAMA));

            sb.append("ID   : ").append(id).append("\n");
            sb.append("NRP  : ").append(n).append("\n");
            sb.append("Nama : ").append(nm).append("\n");
            sb.append("----------------------\n");
        }
        cursor.close();

        tvHasil.setText(sb.toString());
        Toast.makeText(this, "Data berhasil diambil", Toast.LENGTH_SHORT).show();
    }

    // UPDATE
    private void updateData() {
        String nrp = etNrp.getText().toString().trim();
        String namaBaru = etNama.getText().toString().trim();

        if (nrp.isEmpty() || namaBaru.isEmpty()) {
            Toast.makeText(this, "NRP & Nama baru harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        int rows = dbHelper.updateData(nrp, namaBaru);
        if (rows > 0) {
            Toast.makeText(this, rows + " data berhasil diupdate", Toast.LENGTH_SHORT).show();
            tvHasil.setText("✔ Data NRP " + nrp + " diupdate menjadi: " + namaBaru);
            kosongkanField();
        } else {
            Toast.makeText(this, "Data dengan NRP " + nrp + " tidak ditemukan!", Toast.LENGTH_SHORT).show();
        }
    }

    // HAPUS
    private void hapusData() {
        String nrp = etNrp.getText().toString().trim();

        if (nrp.isEmpty()) {
            Toast.makeText(this, "Masukkan NRP yang akan dihapus!", Toast.LENGTH_SHORT).show();
            return;
        }

        int rows = dbHelper.hapusData(nrp);
        if (rows > 0) {
            Toast.makeText(this, rows + " data berhasil dihapus", Toast.LENGTH_SHORT).show();
            tvHasil.setText("✔ Data dengan NRP " + nrp + " telah dihapus.");
            kosongkanField();
        } else {
            Toast.makeText(this, "Data dengan NRP " + nrp + " tidak ditemukan!", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper untuk mengosongkan EditText
    private void kosongkanField() {
        etNrp.setText("");
        etNama.setText("");
        etNrp.requestFocus();
    }
}
