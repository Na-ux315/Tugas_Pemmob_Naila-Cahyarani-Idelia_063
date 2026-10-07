app -> kotlin+java -> com.example.testkalkulator -> MainActivity.java

  package com.example.testintent;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etNrp, etNama;
    private RadioGroup rgJenisKelamin;
    private RadioButton rbCowok, rbCewek;
    private Button btnNext;

    public static final String EXTRA_NRP           = "extra_nrp";
    public static final String EXTRA_NAMA          = "extra_nama";
    public static final String EXTRA_JENIS_KELAMIN = "extra_jenis_kelamin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNrp           = findViewById(R.id.etNrp);
        etNama          = findViewById(R.id.etNama);
        rgJenisKelamin  = findViewById(R.id.rgJenisKelamin);
        rbCowok      = findViewById(R.id.rbCowok);
        rbCewek     = findViewById(R.id.rbCewek);
        btnNext         = findViewById(R.id.btnNext);

        btnNext.setOnClickListener(v -> {
            String nrp  = etNrp.getText().toString().trim();
            String nama = etNama.getText().toString().trim();

            // Validasi NRP
            if (TextUtils.isEmpty(nrp)) {
                etNrp.setError("NRP tidak boleh kosong");
                etNrp.requestFocus();
                return;
            }

            // Validasi Nama
            if (TextUtils.isEmpty(nama)) {
                etNama.setError("Nama tidak boleh kosong");
                etNama.requestFocus();
                return;
            }

            // Validasi Option Button (harus pilih salah satu)
            if (rgJenisKelamin.getCheckedRadioButtonId() == -1) {
                Toast.makeText(MainActivity.this,
                        "Pilih jenis kelamin dulu", Toast.LENGTH_SHORT).show();
                return;
            }

            // Ambil jenis kelamin yang dipilih
            String jenisKelamin = rbCowok.isChecked() ? "Cowok" : "Cewek";

            // Kirim ke Activity 2
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            intent.putExtra(EXTRA_NRP, nrp);
            intent.putExtra(EXTRA_NAMA, nama);
            intent.putExtra(EXTRA_JENIS_KELAMIN, jenisKelamin);
            startActivity(intent);
        });
    }
}
