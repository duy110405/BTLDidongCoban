package com.example.didongbtl;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import Adapter.DiemmonAdapter;
import DoiTuong.DiemMonHoc;

public class activity_bangdiem extends AppCompatActivity {

    private LinearLayout navHome, navLichhoc, navBamgio, navNhiemvu;
    private ImageButton btnSetting , btnQuaylai;
    private RecyclerView rvDiemMonHoc;

    private EditText edtTbcthang10, edtTbcthang4, edtHocluc, edtSotin;

    private List<DiemMonHoc> diemMonHocList;
    private DiemmonAdapter diemmonAdapter;

    // Khai báo database
    private static final String DB_PATH_SUFFIX = "/databases/";
    private static final String DATABASE_NAME = "QuanLySQLDiDong.db";
    private SQLiteDatabase database = null;
    private int currentIdHocSinh = -1;
    private DBQuanLyHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bangdiem);

        // Ánh xạ View
        rvDiemMonHoc = findViewById(R.id.rvDiemMonHoc);
        edtTbcthang10 = findViewById(R.id.edtTbcthang10);
        edtTbcthang4  = findViewById(R.id.edtTbcthang4);
        edtHocluc     = findViewById(R.id.edtHocluc);
        edtSotin      = findViewById(R.id.edtSotin);

        // ✅ Lấy Id học sinh từ SESSION (lúc login đã lưu)
        currentIdHocSinh = getSharedPreferences("SESSION", MODE_PRIVATE)
                .getInt("ID_HOC_SINH", -1);

        if (currentIdHocSinh == -1) {
            Toast.makeText(this, "Không tìm thấy ID học sinh, hãy đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ✅ Dùng DBQuanLyHelper để mở DB (nó tự copy từ assets)
        dbHelper = new DBQuanLyHelper(this);
        database = dbHelper.getDatabase();

        // ================== RecyclerView ==================
        diemMonHocList = new ArrayList<>();
        diemmonAdapter = new DiemmonAdapter(this, diemMonHocList);
        rvDiemMonHoc.setLayoutManager(new LinearLayoutManager(this));
        rvDiemMonHoc.setAdapter(diemmonAdapter);
        rvDiemMonHoc.setNestedScrollingEnabled(false);

        // Load dữ liệu điểm từ SQL
        loadDiemMon();

        // ================== Menu & Header ==================
        navHome = findViewById(R.id.navHome);
        navLichhoc = findViewById(R.id.navLichhoc);
        navBamgio = findViewById(R.id.navBamgio);
        navNhiemvu = findViewById(R.id.navNhiemvu);
        btnSetting = findViewById(R.id.btnSetting);
        btnQuaylai = findViewById(R.id.btnQuaylai);

        btnQuaylai.setOnClickListener(v -> {
            // quay lại màn trước
            finish();
        });

        btnSetting.setOnClickListener(v -> {
            Intent intent = new Intent(activity_bangdiem.this , activity_setting.class);
            startActivity(intent);
        });

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(activity_bangdiem.this, activity_trangchu.class);
            startActivity(intent);
        });

        navLichhoc.setOnClickListener(v -> {
            Intent intent = new Intent(activity_bangdiem.this, activity_lichhoc.class);
            startActivity(intent);
        });

        navBamgio.setOnClickListener(v -> {
            Intent intent = new Intent(activity_bangdiem.this, activity_bamgio.class);
            startActivity(intent);
        });

        navNhiemvu.setOnClickListener(v -> {
            Intent intent = new Intent(activity_bangdiem.this, activity_nhiemvu.class);
            startActivity(intent);
        });
    }


    // ==================================== HÀM LOAD DỮ LIỆU SQL ==========================//
    private void loadDiemMon() {
        diemMonHocList.clear();

        if (database == null || !database.isOpen()) {
            database = dbHelper.getDatabase();
        }

        String sql = "SELECT Id, TenMon, SoTin, ThangDiem10, ThangDiem4, ThangDiemChu, GhiChu " +
                "FROM DiemMon WHERE IdHocSinh = ?";

        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(currentIdHocSinh)});

        int tongTin = 0;
        double tongDiem10 = 0;
        double tongDiem4  = 0;

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String tenMon = cursor.getString(1);
                int soTin = cursor.getInt(2);
                double thangDiem10 = cursor.getDouble(3);
                double thangDiem4  = cursor.getDouble(4);
                String thangDiemChu = cursor.getString(5);
                String ghiChu = cursor.getString(6);

                diemMonHocList.add(new DiemMonHoc(
                        id, tenMon, soTin, thangDiem10, thangDiem4, thangDiemChu, ghiChu
                ));

                tongTin += soTin;
                tongDiem10 += soTin * thangDiem10;
                tongDiem4  += soTin * thangDiem4;

            } while (cursor.moveToNext());
        }

        cursor.close();
        diemmonAdapter.notifyDataSetChanged();

        if (tongTin > 0) {
            double tbc10 = tongDiem10 / tongTin;
            double tbc4  = tongDiem4  / tongTin;

            edtTbcthang10.setText(String.format(java.util.Locale.getDefault(), "%.2f", tbc10));
            edtTbcthang4.setText(String.format(java.util.Locale.getDefault(), "%.2f", tbc4));
            edtSotin.setText(String.valueOf(tongTin));

            String hocLuc;
            if (tbc4 >= 3.6) {
                hocLuc = "Xuất sắc";
            } else if (tbc4 >= 3.2) {
                hocLuc = "Giỏi";
            } else if (tbc4 >= 2.5) {
                hocLuc = "Khá";
            } else if (tbc4 >= 2.0) {
                hocLuc = "Trung bình";
            } else {
                hocLuc = "Yếu";
            }
            edtHocluc.setText(hocLuc);

        } else {
            edtTbcthang10.setText("");
            edtTbcthang4.setText("");
            edtSotin.setText("0");
            edtHocluc.setText("");
            Toast.makeText(this,
                    "Chưa có dữ liệu điểm môn học cho sinh viên này",
                    Toast.LENGTH_SHORT).show();
        }
    }


}
