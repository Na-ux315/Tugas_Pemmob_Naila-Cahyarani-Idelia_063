app -> kotlin+java -> com.example.testkalkulator -> MainActivity.java

package com.example.testcamera; // GANTI sesuai package name kamu

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int KODE_PERMISSION_KAMERA = 100;
    private static final int KODE_REQUEST_KAMERA = 200;

    ImageView imageViewPreview;
    TextView tvPath;
    Button btnBukaKamera, btnLihatFolder;

    // Variabel untuk menyimpan lokasi file foto
    private File fileFoto;
    private Uri uriFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewPreview = findViewById(R.id.imageViewPreview);
        tvPath = findViewById(R.id.tvPath);
        btnBukaKamera = findViewById(R.id.btnBukaKamera);
        btnLihatFolder = findViewById(R.id.btnLihatFolder);

        // Tombol buka kamera
        btnBukaKamera.setOnClickListener(v -> cekIzinDanBukaKamera());

        // Tombol lihat folder
        btnLihatFolder.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListFotoActivity.class);
            startActivity(intent);
        });
    }

    // --- CEK IZIN KAMERA ---
    private void cekIzinDanBukaKamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Minta izin kalau belum ada
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    KODE_PERMISSION_KAMERA);
        } else {
            // Izin sudah ada, langsung buka kamera
            bukaKamera();
        }
    }

    // --- HASIL PERMINTAAN IZIN ---
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == KODE_PERMISSION_KAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bukaKamera();
            } else {
                Toast.makeText(this, "Izin kamera ditolak!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // --- MEMBUKA KAMERA BAWAAN ---
    private void bukaKamera() {
        // Buat file kosong dengan nama format tanggal waktu
        fileFoto = buatFileFoto();

        if (fileFoto == null) {
            Toast.makeText(this, "Gagal membuat file foto", Toast.LENGTH_SHORT).show();
            return;
        }

        // Dapatkan URI aman lewat FileProvider
        uriFoto = FileProvider.getUriForFile(
                this,
                getPackageName() + ".fileprovider", // harus sama dengan authorities di Manifest
                fileFoto
        );

        // Intent ke kamera
        Intent intentKamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentKamera.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
        intentKamera.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        // Jalankan kamera (kalau ada aplikasi kamera)
        if (intentKamera.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intentKamera, KODE_REQUEST_KAMERA);
        } else {
            Toast.makeText(this, "Tidak ada aplikasi kamera!", Toast.LENGTH_SHORT).show();
        }
    }

    // --- MEMBUAT FILE FOTO DENGAN NAMA FORMAT TANGGAL ---
    private File buatFileFoto() {
        // Format: IMG_YYYYMMDD_HHMMSS
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String namaFile = "IMG_" + timeStamp + ".jpg";

        // Folder: /Android/data/<package>/files/Pictures
        File folder = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Pastikan folder ada
        if (folder != null && !folder.exists()) {
            folder.mkdirs();
        }

        try {
            return new File(folder, namaFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // --- HASIL DARI KAMERA ---
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == KODE_REQUEST_KAMERA) {
            if (resultCode == RESULT_OK) {
                // Tampilkan preview foto
                imageViewPreview.setImageURI(uriFoto);

                // Tampilkan path file
                tvPath.setText("Tersimpan: " + fileFoto.getAbsolutePath());

                Toast.makeText(this, "Foto berhasil disimpan!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Pengambilan foto dibatalkan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // --- LIFECYCLE (BONUS BUAT TUGAS) ---
    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, "onStart dipanggil", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(this, "onStop dipanggil", Toast.LENGTH_SHORT).show();
    }
}
