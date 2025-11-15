package com.example.didongbtl;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import Adapter.LichhocAdapter;
import DoiTuong.LichHoc;

public class activity_lichhoc extends AppCompatActivity {

    private LinearLayout navHome, navLichhoc, navBamgio, navNhiemvu;
    private ImageButton btnSetting , btnQuaylai;
    RecyclerView rvLichhocSchedule ;
    private EditText edtNgayhoc;
    private List<LichHoc> lichHocList;
    private LichhocAdapter lichhocAdapter;

    // Khai báo database
    String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database=null;
    String DATABASE_NAME="QuanLySQLDiDong.db";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lichhoc);
        //==== Ánh xạ View ====//
        rvLichhocSchedule = findViewById(R.id.rvLichhocSchedule);
        edtNgayhoc = findViewById(R.id.edtNgayhoc);
        // EditText ngày: không cho gõ bàn phím, chỉ bấm chọn
        edtNgayhoc.setFocusable(false);
        edtNgayhoc.setClickable(true);

        //Gọi hàm Copy CSDL từ assets vào thư mục Databases
        processCopy();
        //Mở CSDL lên để dùng
        database = openOrCreateDatabase("QuanLySQLDiDong.db",MODE_PRIVATE, null);
        // ================== RecyclerView ==================
        lichHocList = new ArrayList<>();
        lichhocAdapter = new LichhocAdapter(this, lichHocList);
        rvLichhocSchedule.setLayoutManager(new LinearLayoutManager(this));
        rvLichhocSchedule.setAdapter(lichhocAdapter);
        rvLichhocSchedule.setNestedScrollingEnabled(false);

        // ================== Set ngày hôm nay ==================
        Calendar cal = Calendar.getInstance();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(cal.getTime());
        edtNgayhoc.setText(today);

        // Load lịch cho ngày hôm nay
        loadLichHocForDate(today);

        // ================== Chọn ngày bằng DatePicker ==================
        edtNgayhoc.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            try {
                // nếu đang hiển thị ngày nào thì mở picker tới ngày đó
                String current = edtNgayhoc.getText().toString();
                if (!current.isEmpty()) {
                    // yyyy-MM-dd
                    String[] parts = current.split("-");
                    int y = Integer.parseInt(parts[0]);
                    int m = Integer.parseInt(parts[1]) - 1;
                    int d = Integer.parseInt(parts[2]);
                    c.set(y, m, d);
                }
            } catch (Exception ignored) {}

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    activity_lichhoc.this,
                    (view, y, m, d) -> {
                        String selected = String.format(Locale.getDefault(),
                                "%04d-%02d-%02d", y, (m + 1), d);
                        edtNgayhoc.setText(selected);
                        loadLichHocForDate(selected);
                    },
                    year, month, day
            );
            dialog.show();
        });


        // =================================================   Gọi inent chuyển trang cho menu , header ========================================== //
        navHome = findViewById(R.id.navHome);
        navLichhoc = findViewById(R.id.navLichhoc);
        navBamgio = findViewById(R.id.navBamgio);
        navNhiemvu = findViewById(R.id.navNhiemvu);
        btnSetting = findViewById(R.id.btnSetting);
        btnQuaylai = findViewById(R.id.btnQuaylai);

        btnQuaylai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(activity_lichhoc.this , activity_trangchu.class);
                finish();
            }
        });
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_lichhoc.this , activity_setting.class) ;
                startActivity(intent);
            }
        });

        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_lichhoc.this, activity_trangchu.class);
                startActivity(intent);
            }
        });
        navLichhoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_lichhoc.this, activity_lichhoc.class);
                startActivity(intent);
            }
        });
        navBamgio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_lichhoc.this, activity_bamgio.class);
                startActivity(intent);
            }
        });
        navNhiemvu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_lichhoc.this, activity_nhiemvu.class);
                startActivity(intent);
            }
        });
        // ======================================================================================================================//

        rvLichhocSchedule = findViewById(R.id.rvLichhocSchedule);
                // giúp màn hình cuộn mượt hơn .
                rvLichhocSchedule.setNestedScrollingEnabled(false) ;
    }

    // -=========================================Hàm copy csdl =====================================
    private void processCopy() {
        //private app
        File dbFile = getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists())
        {
            try{CopyDataBaseFromAsset();
                Toast.makeText(this, "Copying sucess from Assets folder",
                        Toast.LENGTH_LONG).show();
            }
            catch (Exception e){
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
    private String getDatabasePath() {
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX+ DATABASE_NAME;
    }

    public void CopyDataBaseFromAsset() {
           // TODO Auto-generated method stub
        try {
            InputStream myInput;
            myInput = getAssets().open(DATABASE_NAME);
          // Path to the just created empty db
            String outFileName = getDatabasePath();
          // if the path doesn't exist first, create it
            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!f.exists())
                f.mkdir();
         // Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);
         // transfer bytes from the inputfile to the outputfile
         // Truyền bytes dữ liệu từ input đến output
            int size = myInput.available();
            byte[] buffer = new byte[size];
            myInput.read(buffer);
            myOutput.write(buffer);
         // Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (IOException e) {
         // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    // ==================================== HÀM LOAD DỮ LIỆU SQL ==========================//
    private void loadLichHocForDate(String ngayHoc) {
        lichHocList.clear();

        if (database == null) {
            database = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        }

        String sql = "SELECT MaLich, TenMon, GioBatDau, PhongHoc, HinhThucHoc, NgayHoc " +
                "FROM LichHoc WHERE NgayHoc = ? " +
                "ORDER BY GioBatDau ASC";

        Cursor cursor = database.rawQuery(sql, new String[]{ngayHoc});

        if (cursor.moveToFirst()) {
            do {
                int maLich = cursor.getInt(0);
                String tenMon = cursor.getString(1);
                String gioBatDau = cursor.getString(2);
                String phongHoc = cursor.getString(3);
                String hinhThucHoc = cursor.getString(4);
                String ngay = cursor.getString(5);

                lichHocList.add(new LichHoc(
                        maLich, tenMon, gioBatDau, phongHoc, hinhThucHoc, ngay
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();

        lichhocAdapter.notifyDataSetChanged();

        if (lichHocList.isEmpty()) {
            Toast.makeText(this,
                    "Không có lịch học cho ngày " + ngayHoc,
                    Toast.LENGTH_SHORT).show();
        }
    }
}