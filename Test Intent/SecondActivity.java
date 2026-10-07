app -> kotlin+java -> com.example.testkalkulator -> SecondActivity.java

package com.example.testintent;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "SecondActivity";

    private TextView tvNrp, tvNama, tvJenisKelamin;
    private Button btnTutup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        tvNrp           = findViewById(R.id.tvNrp);
        tvNama          = findViewById(R.id.tvNama);
        tvJenisKelamin  = findViewById(R.id.tvJenisKelamin);
        btnTutup        = findViewById(R.id.btnTutup);

        // Ambil data dari Intent
        String nrp          = getIntent().getStringExtra(MainActivity.EXTRA_NRP);
        String nama         = getIntent().getStringExtra(MainActivity.EXTRA_NAMA);
        String jenisKelamin = getIntent().getStringExtra(MainActivity.EXTRA_JENIS_KELAMIN);

        tvNrp.setText(nrp != null ? nrp : "-");
        tvNama.setText(nama != null ? nama : "-");
        tvJenisKelamin.setText(jenisKelamin != null ? jenisKelamin : "-");

        // Tombol Tutup -> finish() -> onDestroy() akan dipanggil
        btnTutup.setOnClickListener(v -> {
            Log.d(TAG, "Tombol Tutup ditekan, memanggil finish()");
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() dipanggil - Activity 2 dihancurkan");
    }
}
