app -> kotlin+java -> com.example.testkalkulator -> MainActivity.java

package com.example.testsensor1;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private TextView tvLux, tvStatus, tvSensorInfo;

    // Threshold hysteresis biar tema tidak flicker
    private static final float DARK_THRESHOLD = 10.0f;   // < 10 lux  -> Dark Mode
    private static final float LIGHT_THRESHOLD = 80.0f;  // > 80 lux  -> Light Mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLux = findViewById(R.id.tvLux);
        tvStatus = findViewById(R.id.tvStatus);
        tvSensorInfo = findViewById(R.id.tvSensorInfo);

        // 1. Inisialisasi SensorManager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // 2. Ambil instance sensor cahaya
        if (sensorManager != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }

        // 3. Cek ketersediaan sensor (Graceful Fallback)
        if (lightSensor == null) {
            tvLux.setText("Sensor Tidak Ada");
            tvStatus.setText("Perangkat ini tidak memiliki Light Sensor");
            Toast.makeText(this,
                    "Fitur dinonaktifkan: Perangkat tanpa Light Sensor",
                    Toast.LENGTH_LONG).show();
        } else {
            // Tampilkan metadata sensor
            String info = String.format(Locale.getDefault(),
                    "Vendor: %s\nPower: %.2f mA\nResolution: %.4f lux\nMax Range: %.0f lux",
                    lightSensor.getVendor(),
                    lightSensor.getPower(),
                    lightSensor.getResolution(),
                    lightSensor.getMaximumRange());
            tvSensorInfo.setText(info);
        }
    }

    // 4. Daftarkan listener saat Activity kembali ke foreground
    @Override
    protected void onResume() {
        super.onResume();
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor,
                    SensorManager.SENSOR_DELAY_UI);
        }
    }

    // 5. WAJIB lepas listener saat Activity ke background (hemat baterai)
    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    // 6. Callback utama saat nilai lux berubah
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lux = event.values[0];

            // Update UI
            tvLux.setText(String.format(Locale.getDefault(), "%.1f Lux", lux));

            // Logika dark mode otomatis dengan hysteresis
            if (lux < DARK_THRESHOLD) {
                tvStatus.setText("Status: GELAP → Dark Mode");
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES);
            } else if (lux > LIGHT_THRESHOLD) {
                tvStatus.setText("Status: TERANG → Light Mode");
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                tvStatus.setText("Status: REDUP (zona hysteresis)");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Tidak dipakai untuk light sensor
    }
}
