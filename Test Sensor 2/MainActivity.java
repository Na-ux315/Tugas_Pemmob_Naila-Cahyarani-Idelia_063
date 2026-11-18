app -> kotlin+java -> com.example.testkalkulator -> MainActivity.java

package com.example.testsensor2;

import android.content.Context;
import android.graphics.Color;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private CompassView compassView;
    private TextView tvDegree;
    private TextView tvStatus;

    // Koordinat Ka'bah
    private static final double KAABA_LAT = 21.4225;
    private static final double KAABA_LON = 39.8262;

    // Ganti dengan lokasimu (contoh: Jakarta)
    private static final double USER_LAT = -6.2088;
    private static final double USER_LON = 106.8456;

    // Makin kecil = makin halus tapi makin lambat respons
    private static final float ALPHA = 0.12f;

    private float qiblaDegree = 0f;
    private float declination = 0f;

    private final float[] gravityData = new float[3];
    private final float[] magneticData = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] inclinationMatrix = new float[9];
    private final float[] orientation = new float[3];

    private boolean hasGravity = false;
    private boolean hasMagnetic = false;
    private int magAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compassView = findViewById(R.id.compassView);
        tvDegree = findViewById(R.id.tvDegree);
        tvStatus = findViewById(R.id.tvStatus);

        qiblaDegree = hitungArahKiblat(
                USER_LAT, USER_LON, KAABA_LAT, KAABA_LON);

        // Koreksi utara magnetik -> utara sejati
        GeomagneticField field = new GeomagneticField(
                (float) USER_LAT, (float) USER_LON, 0f,
                System.currentTimeMillis());
        declination = field.getDeclination();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }

        if (accelerometer == null || magnetometer == null) {
            tvStatus.setText("Sensor accelerometer atau magnetometer tidak tersedia");
        } else {
            tvStatus.setText("Arahkan HP untuk mencari kiblat");
        }

        compassView.updateAzimuth(0f, qiblaDegree);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {
            if (accelerometer != null) {
                sensorManager.registerListener(
                        this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            }
            if (magnetometer != null) {
                sensorManager.registerListener(
                        this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        // Reset supaya tidak memakai data lama saat kembali
        hasGravity = false;
        hasMagnetic = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();

        if (type == Sensor.TYPE_ACCELEROMETER) {
            lowPass(event.values, gravityData, hasGravity);
            hasGravity = true;
        } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            lowPass(event.values, magneticData, hasMagnetic);
            hasMagnetic = true;
        } else {
            return;
        }

        if (!hasGravity || !hasMagnetic) return;

        boolean success = SensorManager.getRotationMatrix(
                rotationMatrix, inclinationMatrix, gravityData, magneticData);
        if (!success) return;

        SensorManager.getOrientation(rotationMatrix, orientation);

        float azimuth = (float) Math.toDegrees(orientation[0]) + declination;
        azimuth = (azimuth % 360f + 360f) % 360f;

        tvDegree.setText(String.format(Locale.getDefault(), "%.0f°", azimuth));
        compassView.updateAzimuth(azimuth, qiblaDegree);

        // Selisih terkecil antara arah HP dan arah kiblat (±5° dianggap tepat)
        float selisih = Math.abs(((azimuth - qiblaDegree + 540f) % 360f) - 180f);

        if (selisih <= 5f) {
            tvDegree.setTextColor(Color.rgb(0, 255, 100));
            tvStatus.setText("Arah kiblat sudah tepat");
        } else {
            tvDegree.setTextColor(Color.WHITE);
            if (magAccuracy <= SensorManager.SENSOR_STATUS_ACCURACY_LOW) {
                tvStatus.setText("Akurasi rendah: gerakkan HP membentuk angka 8 untuk kalibrasi");
            } else {
                tvStatus.setText(String.format(Locale.getDefault(),
                        "Arah kiblat %.0f° dari utara", qiblaDegree));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magAccuracy = accuracy;
        }
    }

    // Low-pass filter: out += ALPHA * (in - out)
    private void lowPass(float[] in, float[] out, boolean initialized) {
        if (!initialized) {
            System.arraycopy(in, 0, out, 0, 3);
            return;
        }
        for (int i = 0; i < 3; i++) {
            out[i] += ALPHA * (in[i] - out[i]);
        }
    }

    // Bearing ke Ka'bah
    private float hitungArahKiblat(
            double lat1, double lon1, double lat2, double lon2) {
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double deltaLambda = Math.toRadians(lon2 - lon1);

        double y = Math.sin(deltaLambda) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2)
                - Math.sin(phi1) * Math.cos(phi2) * Math.cos(deltaLambda);

        double bearing = Math.toDegrees(Math.atan2(y, x));
        return (float) ((bearing + 360.0) % 360.0);
    }
}
