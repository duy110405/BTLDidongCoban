package com.example.didongbtl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import Adapter.NhiemvuAdapter;
import DoiTuong.NhiemVu;

public class activity_nhiemvu extends AppCompatActivity {

    private static final int REQUEST_ADD_TASK = 1001;

    private LinearLayout navHome, navLichhoc, navBamgio, navNhiemvu;
    private ImageButton btnSetting, btnQuaylai;
    private Button btnThemnhiemvu;
    private RecyclerView rvNhiemvu;

    private TaskDatabaseHelper dbHelper;
    private List<NhiemVu> nhiemVuList;
    private NhiemvuAdapter adapter;

    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nhiemvu);

        // Lấy user đang đăng nhập
        currentUserId = SessionManager.getUserId(this);
        if (currentUserId == -1) {
            // Chưa đăng nhập -> quay về màn signin
            Intent intent = new Intent(activity_nhiemvu.this, activity_signin.class);
            startActivity(intent);
            finish();
            return;
        }

        // Ánh xạ view
        btnThemnhiemvu = findViewById(R.id.btnThemnhiemvu);
        rvNhiemvu = findViewById(R.id.rvNhiemvu);

        navHome = findViewById(R.id.navHome);
        navLichhoc = findViewById(R.id.navLichhoc);
        navBamgio = findViewById(R.id.navBamgio);
        navNhiemvu = findViewById(R.id.navNhiemvu);
        btnSetting = findViewById(R.id.btnSetting);
        btnQuaylai = findViewById(R.id.btnQuaylai);

        // SQLite helper
        dbHelper = new TaskDatabaseHelper(this);

        // RecyclerView
        nhiemVuList = new ArrayList<>();
        adapter = new NhiemvuAdapter(this, nhiemVuList, dbHelper);
        rvNhiemvu.setLayoutManager(new LinearLayoutManager(this));
        rvNhiemvu.setAdapter(adapter);
        rvNhiemvu.setNestedScrollingEnabled(false);

        loadTasksFromDb();

        // Mở màn thêm nhiệm vụ
        btnThemnhiemvu.setOnClickListener(v -> {
            Intent intent = new Intent(activity_nhiemvu.this, activity_addnhiemvu.class);
            startActivityForResult(intent, REQUEST_ADD_TASK);
        });

        // Header & bottom menu
        btnQuaylai.setOnClickListener(v -> {
            Intent intent = new Intent(activity_nhiemvu.this, activity_trangchu.class);
            startActivity(intent);
            finish();
        });

        btnSetting.setOnClickListener(v -> {
            Intent intent = new Intent(activity_nhiemvu.this, activity_setting.class);
            startActivity(intent);
        });

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(activity_nhiemvu.this, activity_trangchu.class);
            startActivity(intent);
        });

        navLichhoc.setOnClickListener(v -> {
            Intent intent = new Intent(activity_nhiemvu.this, activity_lichhoc.class);
            startActivity(intent);
        });

        navBamgio.setOnClickListener(v -> {
            Intent intent = new Intent(activity_nhiemvu.this, activity_bamgio.class);
            startActivity(intent);
        });

        navNhiemvu.setOnClickListener(v -> {
            // đang ở màn này rồi, không làm gì
        });
    }

    private void loadTasksFromDb() {
        nhiemVuList.clear();
        // 🔥 chỉ lấy nhiệm vụ của currentUserId
        nhiemVuList.addAll(dbHelper.getTasksForUser(currentUserId));
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_TASK && resultCode == RESULT_OK && data != null) {
            String ten = data.getStringExtra("tenTask");
            String moTa = data.getStringExtra("moTa");
            String gioHetHan = data.getStringExtra("gioHetHan");
            String ngayHetHan = data.getStringExtra("ngayHetHan");
            String mucDo = data.getStringExtra("mucDo");

            if (ten != null && !ten.trim().isEmpty()) {
                // 🔥 lưu nhiệm vụ gắn với currentUserId
                dbHelper.insertTask(
                        ten,
                        gioHetHan != null ? gioHetHan : "",
                        ngayHetHan != null ? ngayHetHan : "",
                        mucDo != null ? mucDo : "",
                        moTa != null ? moTa : "",
                        currentUserId
                );
                loadTasksFromDb();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasksFromDb();
    }
}
