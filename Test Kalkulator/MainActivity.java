app -> kotlin+java -> com.example.testkalkulator -> MainActivity.java

package com.example.testkalkulator;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText edtPanjang, edtLebar;
    private TextView txtHasil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi komponen
        edtPanjang = findViewById(R.id.edtPanjang);
        edtLebar = findViewById(R.id.edtLebar);
        txtHasil = findViewById(R.id.txtHasil);

        // Event klik tombol
        findViewById(R.id.btnHitung).setOnClickListener(v -> hitungLuas());
    }

    private void hitungLuas() {
        String inputPanjang = edtPanjang.getText().toString().trim();
        String inputLebar = edtLebar.getText().toString().trim();

        // Validasi input kosong
        if (inputPanjang.isEmpty() || inputLebar.isEmpty()) {
            txtHasil.setText(getString(R.string.error_kosong));
            return;
        }

        try {
            double panjang = Double.parseDouble(inputPanjang);
            double lebar = Double.parseDouble(inputLebar);
            double luas = panjang * lebar;

            // Pakai resource string dengan placeholder
            txtHasil.setText(getString(R.string.hasil_format, String.valueOf(luas)));
        } catch (NumberFormatException e) {
            txtHasil.setText(getString(R.string.error_invalid));
        }
    }
}
