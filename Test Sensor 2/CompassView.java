app -> kotlin+java -> com.example.testkalkulator -> CompassView.java

package com.example.testsensor2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

public class CompassView extends View {

    private final Paint paintRing = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintRingInner = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintGrid = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintCross = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintLabelBg = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintLabelStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintQibla = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintQiblaThin = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintCenterBg = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintCenterStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintMarker = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path markerPath = new Path();

    private float azimuth = 0f;
    private float qiblaAngle = 0f;
    private float density = 1f;
    private Drawable kaabaIcon;

    private static final int BLUE = Color.rgb(50, 145, 180);

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        density = getResources().getDisplayMetrics().density;

        paintRing.setColor(Color.WHITE);
        paintRing.setStyle(Paint.Style.STROKE);
        paintRing.setStrokeWidth(2f * density);

        paintRingInner.setColor(BLUE);
        paintRingInner.setStyle(Paint.Style.STROKE);
        paintRingInner.setStrokeWidth(1f * density);

        paintGrid.setColor(BLUE);
        paintGrid.setStyle(Paint.Style.STROKE);
        paintGrid.setStrokeWidth(1f * density);

        paintCross.setColor(Color.WHITE);
        paintCross.setStyle(Paint.Style.STROKE);
        paintCross.setStrokeWidth(1.5f * density);

        paintText.setColor(Color.WHITE);
        paintText.setTextSize(18f * density);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setFakeBoldText(true);

        paintLabelBg.setColor(Color.BLACK);
        paintLabelBg.setStyle(Paint.Style.FILL);

        paintLabelStroke.setColor(Color.WHITE);
        paintLabelStroke.setStyle(Paint.Style.STROKE);
        paintLabelStroke.setStrokeWidth(1.5f * density);

        // Garis kiblat: tebal di dalam ring
        paintQibla.setColor(Color.rgb(0, 255, 0));
        paintQibla.setStyle(Paint.Style.STROKE);
        paintQibla.setStrokeWidth(4f * density);
        paintQibla.setStrokeCap(Paint.Cap.ROUND);

        // Garis kiblat: tipis di luar ring menuju ikon
        paintQiblaThin.setColor(BLUE);
        paintQiblaThin.setStyle(Paint.Style.STROKE);
        paintQiblaThin.setStrokeWidth(1.2f * density);

        paintCenterBg.setColor(Color.BLACK);
        paintCenterBg.setStyle(Paint.Style.FILL);

        paintCenterStroke.setColor(Color.WHITE);
        paintCenterStroke.setStyle(Paint.Style.STROKE);
        paintCenterStroke.setStrokeWidth(2f * density);

        paintMarker.setColor(Color.WHITE);
        paintMarker.setStyle(Paint.Style.FILL);

        kaabaIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_cube);
    }

    public void updateAzimuth(float azimuth, float qiblaAngle) {
        this.azimuth = normalize(azimuth);
        this.qiblaAngle = normalize(qiblaAngle);
        invalidate();
    }

    private float normalize(float angle) {
        return (angle % 360f + 360f) % 360f;
    }

    // Titik pada sudut tertentu (0° = atas, searah jarum jam)
    private float px(float cx, float angleDeg, float r) {
        return cx + (float) Math.sin(Math.toRadians(angleDeg)) * r;
    }

    private float py(float cy, float angleDeg, float r) {
        return cy - (float) Math.cos(Math.toRadians(angleDeg)) * r;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        // Sisakan ruang di luar ring untuk garis silang & ikon Ka'bah
        float R = Math.min(cx, cy) * 0.72f;
        if (R <= 0f) return;

        // ===== Bagian tetap: penanda arah depan HP (segitiga di atas) =====
        float markerTip = cy - R * 1.24f;
        float markerBase = cy - R * 1.13f;
        float markerHalf = R * 0.05f;
        markerPath.reset();
        markerPath.moveTo(cx, markerTip);
        markerPath.lineTo(cx - markerHalf, markerBase);
        markerPath.lineTo(cx + markerHalf, markerBase);
        markerPath.close();
        canvas.drawPath(markerPath, paintMarker);

        // ===== Bagian yang ikut berputar =====
        canvas.save();
        canvas.rotate(-azimuth, cx, cy);

        // Ring luar (putih) + ring dalam (biru)
        canvas.drawCircle(cx, cy, R, paintRing);
        canvas.drawCircle(cx, cy, R * 0.96f, paintRingInner);

        // Lingkaran bantu
        canvas.drawCircle(cx, cy, R * 0.40f, paintGrid);
        canvas.drawCircle(cx, cy, R * 0.62f, paintGrid);
        canvas.drawCircle(cx, cy, R * 0.85f, paintGrid);

        // Garis radial biru tiap 30°
        for (int deg = 0; deg < 360; deg += 30) {
            canvas.drawLine(
                    px(cx, deg, R * 0.30f), py(cy, deg, R * 0.30f),
                    px(cx, deg, R), py(cy, deg, R),
                    paintGrid);
        }

        // Garis silang putih, menjulur keluar ring
        float ext = R * 1.10f;
        canvas.drawLine(cx - ext, cy, cx + ext, cy, paintCross);
        canvas.drawLine(cx, cy - ext, cx, cy + ext, paintCross);

        // Garis kiblat
        float thickEnd = R * 0.90f;
        float iconDist = R * 1.20f;
        canvas.drawLine(
                cx, cy,
                px(cx, qiblaAngle, thickEnd), py(cy, qiblaAngle, thickEnd),
                paintQibla);
        canvas.drawLine(
                px(cx, qiblaAngle, thickEnd), py(cy, qiblaAngle, thickEnd),
                px(cx, qiblaAngle, iconDist), py(cy, qiblaAngle, iconDist),
                paintQiblaThin);

        // Huruf N, E, S, W dalam lingkaran (tetap tegak)
        float labelDist = R * 0.62f;
        float labelR = R * 0.11f;
        String[] labels = {"N", "E", "S", "W"};
        for (int i = 0; i < 4; i++) {
            float lx = px(cx, i * 90f, labelDist);
            float ly = py(cy, i * 90f, labelDist);
            canvas.save();
            canvas.rotate(azimuth, lx, ly); // batalkan putaran supaya huruf tegak
            canvas.drawCircle(lx, ly, labelR, paintLabelBg);
            canvas.drawCircle(lx, ly, labelR, paintLabelStroke);
            float textY = ly - (paintText.ascent() + paintText.descent()) / 2f;
            canvas.drawText(labels[i], lx, textY, paintText);
            canvas.restore();
        }

        // Ikon Ka'bah (tetap tegak, tidak ikut miring)
        if (kaabaIcon != null) {
            float ix = px(cx, qiblaAngle, iconDist);
            float iy = py(cy, qiblaAngle, iconDist);
            int size = Math.round(R * 0.30f);
            canvas.save();
            canvas.rotate(azimuth, ix, iy);
            kaabaIcon.setBounds(
                    Math.round(ix - size / 2f), Math.round(iy - size / 2f),
                    Math.round(ix + size / 2f), Math.round(iy + size / 2f));
            kaabaIcon.draw(canvas);
            canvas.restore();
        }

        canvas.restore();

        // ===== Lingkaran tengah (tempat tvDegree) =====
        float centerR = R * 0.30f;
        canvas.drawCircle(cx, cy, centerR, paintCenterBg);
        canvas.drawCircle(cx, cy, centerR, paintCenterStroke);
    }
}
