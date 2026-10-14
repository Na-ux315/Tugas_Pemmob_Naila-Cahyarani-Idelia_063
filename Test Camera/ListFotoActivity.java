app -> kotlin+java -> com.example.testkalkulator -> ListFotoActivity.java

package com.example.testcamera; // GANTI sesuai package name kamu

import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class ListFotoActivity extends AppCompatActivity {

    ListView listViewFoto;
    Button btnKembali;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_foto);

        listViewFoto = findViewById(R.id.listViewFoto);
        btnKembali = findViewById(R.id.btnKembali);

        btnKembali.setOnClickListener(v -> finish());

        tampilkanDaftarFoto();
    }

    private void tampilkanDaftarFoto() {
        // Ambil folder Pictures milik aplikasi
        File folder = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        ArrayList<String> daftarFoto = new ArrayList<>();

        if (folder != null && folder.exists()) {
            File[] files = folder.listFiles();

            if (files != null && files.length > 0) {
                for (File f : files) {
                    // Tampilkan nama file + ukuran
                    daftarFoto.add(f.getName() + "\n(" + f.length() / 1024 + " KB)");
                }
            } else {
                daftarFoto.add("Belum ada foto tersimpan");
            }
        } else {
            daftarFoto.add("Folder belum dibuat");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                daftarFoto
        );
        listViewFoto.setAdapter(adapter);

        Toast.makeText(this, "Total: " + daftarFoto.size() + " file", Toast.LENGTH_SHORT).show();
    }
}
