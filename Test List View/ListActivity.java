app -> kotlin+java -> com.example.testkalkulator -> ListActivity.java

package com.example.testlistview;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    ListView listView;
    Button btnKembali;
    DatabaseHelper dbHelper;
    ArrayList<String> listData;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.listViewMahasiswa);
        btnKembali = findViewById(R.id.btnKembali);
        dbHelper = new DatabaseHelper(this);
        listData = new ArrayList<>();

        btnKembali.setOnClickListener(v -> finish());

        tampilkanData();
    }

    private void tampilkanData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME, null);

        listData.clear();
        if (cursor.moveToFirst()) {
            do {
                String data = "NRP: " + cursor.getString(0) + "\nNama: " + cursor.getString(1);
                listData.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Pakai layout item sendiri supaya teks pasti terbaca
        adapter = new ArrayAdapter<>(this, R.layout.item_mahasiswa, R.id.tvItem, listData);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
