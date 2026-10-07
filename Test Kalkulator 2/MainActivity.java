app -> kotlin+java -> com.example.testkalkulator -> MainActivity.java

  package com.example.testkalkulator2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText e1, e2, e3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        e1 = findViewById(R.id.e1);
        e2 = findViewById(R.id.e2);
        e3 = findViewById(R.id.e3);
    }

    // Method yang dipanggil dari android:onClick
    public void tambah(View view) {
        hitung("+");
    }

    public void kurang(View view) {
        hitung("-");
    }

    public void kali(View view) {
        hitung("x");
    }

    public void bagi(View view) {
        hitung(":");
    }

    private void hitung(String operasi) {
        String s1 = e1.getText().toString().trim();
        String s2 = e2.getText().toString().trim();

        if (s1.isEmpty() || s2.isEmpty()) {
            Toast.makeText(this, "Isi kedua bilangan terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double a1 = Double.parseDouble(s1);
            double a2 = Double.parseDouble(s2);
            double hasil = 0;

            switch (operasi) {
                case "+":
                    hasil = a1 + a2;
                    break;
                case "-":
                    hasil = a1 - a2;
                    break;
                case "x":
                    hasil = a1 * a2;
                    break;
                case ":":
                    if (a2 == 0) {
                        Toast.makeText(this, "Tidak bisa dibagi 0!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    hasil = a1 / a2;
                    break;
            }

            // Tampilkan hasil (hilangkan .0 jika bilangan bulat)
            if (hasil == (long) hasil) {
                e3.setText(String.valueOf((long) hasil));
            } else {
                e3.setText(String.valueOf(hasil));
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Input tidak valid!", Toast.LENGTH_SHORT).show();
        }
    }
}
