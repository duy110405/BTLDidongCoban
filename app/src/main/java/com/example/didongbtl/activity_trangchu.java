package com.example.didongbtl;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
import DoiTuong.HocSinh;
import DoiTuong.LichHoc;

public class activity_trangchu extends AppCompatActivity {

    private LinearLayout navHome, navLichhoc, navBamgio, navNhiemvu;
    private ImageButton btnSetting ;
    private TextView tvNgayHoc;
    private RecyclerView rvLichhocHome ;
    private Button btnProfile , btnBangdiem ;
    private List<LichHoc> lichHocList;
    private LichhocAdapter lichhocAdapter;

    // ==== Lưu thông tin sinh viên hiện tại ====
    private HocSinh currentStudent;

    // Khai báo database
    private static final String DB_PATH_SUFFIX = "/databases/";
    private static final String DATABASE_NAME = "QuanLySQLDiDong.db";
    private SQLiteDatabase database = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trangchu);

        // ================== NHẬN HỌC SINH TỪ MÀN ĐĂNG NHẬP ==================
        currentStudent = (HocSinh) getIntent().getSerializableExtra("hoc_sinh");

        //==== Ánh xạ View ====//
        btnBangdiem    = findViewById(R.id.btnBangdiem);
        btnProfile     = findViewById(R.id.btnProfile);
        rvLichhocHome  = findViewById(R.id.rvLichhocHome);
        tvNgayHoc      = findViewById(R.id.tvNgayHoc);

        navHome    = findViewById(R.id.navHome);
        navLichhoc = findViewById(R.id.navLichhoc);
        navBamgio  = findViewById(R.id.navBamgio);
        navNhiemvu = findViewById(R.id.navNhiemvu);
        btnSetting = findViewById(R.id.btnSetting);

        // ================== XỬ LÝ CSDL (copy từ assets nếu chưa có) ==================
        processCopy();
        database = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

        // ================== RecyclerView lịch học ==================
        lichHocList = new ArrayList<>();
        lichhocAdapter = new LichhocAdapter(this, lichHocList);
        rvLichhocHome.setLayoutManager(new LinearLayoutManager(this));
        rvLichhocHome.setAdapter(lichhocAdapter);
        rvLichhocHome.setNestedScrollingEnabled(false);

        // ================== Set ngày hôm nay ==================
        Calendar cal = Calendar.getInstance();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(cal.getTime());
        tvNgayHoc.setText(today);

        // Load lịch cho ngày hôm nay
        loadLichHocForDate(today);

        // ================== Nút Bảng điểm / Profile ==================
        btnBangdiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_trangchu.this , activity_bangdiem.class);
                // nếu sau này cần truyền HocSinh sang bảng điểm thì thêm:
                // intent.putExtra("hoc_sinh", currentStudent);
                startActivity(intent);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_trangchu.this , activity_profile.class);
                // ==== TRUYỀN SINH VIÊN HIỆN TẠI SANG PROFILE ====
                intent.putExtra("hoc_sinh", currentStudent);
                startActivity(intent);
            }
        });

        // ================== HEADER / MENU DƯỚI ==================
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_trangchu.this , activity_setting.class) ;
                // intent.putExtra("hoc_sinh", currentStudent); // nếu cần
                startActivity(intent);
            }
        });

        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đang ở trang chủ rồi, không cần mở lại chính nó
            }
        });

        navLichhoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_trangchu.this, activity_lichhoc.class);
                // intent.putExtra("hoc_sinh", currentStudent);
                startActivity(intent);
            }
        });

        navBamgio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_trangchu.this, activity_bamgio.class);
                // intent.putExtra("hoc_sinh", currentStudent);
                startActivity(intent);
            }
        });

        navNhiemvu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_trangchu.this, activity_nhiemvu.class);
                // intent.putExtra("hoc_sinh", currentStudent);
                startActivity(intent);
            }
        });
    }

    // ================================== HÀM COPY CSDL TỪ ASSETS ==================================
    private void processCopy() {
        File dbFile = getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists()) {
            try {
                CopyDataBaseFromAsset();
                Toast.makeText(this,
                        "Copy CSDL thành công từ Assets",
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getDatabasePathString() {
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX + DATABASE_NAME;
    }

    public void CopyDataBaseFromAsset() {
        try {
            InputStream myInput = getAssets().open(DATABASE_NAME);

            String outFileName = getDatabasePathString();

            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!f.exists()) {
                f.mkdir();
            }

            OutputStream myOutput = new FileOutputStream(outFileName);

            int size = myInput.available();
            byte[] buffer = new byte[size];
            myInput.read(buffer);
            myOutput.write(buffer);

            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ======================== HÀM LOAD LỊCH HỌC THEO NGÀY ========================
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
