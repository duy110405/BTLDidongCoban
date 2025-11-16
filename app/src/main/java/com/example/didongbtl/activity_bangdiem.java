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

        // Copy CSDL từ assets vào thư mục Databases (nếu chưa có)
        processCopy();

        // Mở CSDL
        database = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

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

    // -=========================================Hàm copy csdl =====================================
    private void processCopy() {
        File dbFile = getDatabasePath(DATABASE_NAME);

        // TẠM THỜI: luôn xoá DB cũ để copy lại – tiện khi đang phát triển
        if (dbFile.exists()) {
            dbFile.delete();
        }

        if (!dbFile.exists()) {
            try {
                CopyDataBaseFromAsset();
                Toast.makeText(this, "Copying success from Assets folder",
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
            if (!f.exists()) f.mkdir();

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


    // ==================================== HÀM LOAD DỮ LIỆU SQL ==========================//
    private void loadDiemMon() {
        diemMonHocList.clear();

        if (database == null) {
            database = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        }

        String sql = "SELECT MaMon, TenMon, SoTin, ThangDiem10, ThangDiem4, ThangDiemChu, GhiChu " +
                "FROM DiemMon";

        Cursor cursor = database.rawQuery(sql, null);

        // Biến dùng để tính TBC & số tín tích lũy
        int tongTin = 0;
        double tongDiem10 = 0;
        double tongDiem4  = 0;

        if (cursor.moveToFirst()) {
            do {
                int maMon = cursor.getInt(0);
                String tenMon = cursor.getString(1);
                int soTin = cursor.getInt(2);            // INTEGER
                double thangDiem10 = cursor.getDouble(3); // REAL / INTEGER vẫn đọc được
                double thangDiem4  = cursor.getDouble(4);
                String thangDiemChu = cursor.getString(5);
                String ghiChu = cursor.getString(6);

                // Thêm vào list hiển thị RecyclerView
                diemMonHocList.add(new DiemMonHoc(
                        maMon, tenMon, soTin, thangDiem10, thangDiem4, thangDiemChu, ghiChu
                ));

                // Cộng dồn để tính TBC có trọng số theo số tín
                tongTin += soTin;
                tongDiem10 += soTin * thangDiem10;
                tongDiem4  += soTin * thangDiem4;

            } while (cursor.moveToNext());
        }

        cursor.close();
        diemmonAdapter.notifyDataSetChanged();

        // ------------ Đổ dữ liệu vào 4 EditText ------------
        if (tongTin > 0) {
            double tbc10 = tongDiem10 / tongTin;
            double tbc4  = tongDiem4  / tongTin;

            // Hiển thị 2 số sau dấu phẩy
            edtTbcthang10.setText(String.format(java.util.Locale.getDefault(), "%.2f", tbc10));
            edtTbcthang4.setText(String.format(java.util.Locale.getDefault(), "%.2f", tbc4));
            edtSotin.setText(String.valueOf(tongTin));

            // Xếp loại học lực theo thang 4 (bạn chỉnh lại rule nếu trường khác)
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
            // Không có dòng nào trong bảng DiemMon
            edtTbcthang10.setText("");
            edtTbcthang4.setText("");
            edtSotin.setText("0");
            edtHocluc.setText("");
            Toast.makeText(this,
                    "Chưa có dữ liệu điểm môn học",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
