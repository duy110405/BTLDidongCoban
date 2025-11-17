package com.example.didongbtl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import DoiTuong.HocSinh;

public class activity_profile extends AppCompatActivity {

    private LinearLayout navHome, navLichhoc, navBamgio, navNhiemvu;
    private ImageButton btnSetting , btnQuaylai;

    // Thêm các TextView hiển thị thông tin
    private TextView tvUserName, tvFullName, tvEmail, tvStudentId, tvSchool, tvMajor;

    // Lưu sinh viên hiện tại
    private HocSinh currentStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // ===================== NHẬN HỌC SINH TỪ INTENT =====================
        currentStudent = (HocSinh) getIntent().getSerializableExtra("hoc_sinh");

        // ===================== ÁNH XẠ VIEW =====================
        navHome    = findViewById(R.id.navHome);
        navLichhoc = findViewById(R.id.navLichhoc);
        navBamgio  = findViewById(R.id.navBamgio);
        navNhiemvu = findViewById(R.id.navNhiemvu);
        btnSetting = findViewById(R.id.btnSetting);
        btnQuaylai = findViewById(R.id.btnQuaylai);

        // Các TextView thông tin cá nhân (đã có trong XML bạn gửi)
        tvUserName   = findViewById(R.id.tvUserName);
        tvFullName   = findViewById(R.id.tvFullName);
        tvEmail      = findViewById(R.id.tvEmail);
        tvStudentId  = findViewById(R.id.tvStudentId);
        tvSchool     = findViewById(R.id.tvSchool);
        tvMajor      = findViewById(R.id.tvMajor);

        // ===================== ĐỔ DỮ LIỆU HỌC SINH RA GIAO DIỆN =====================
        if (currentStudent != null) {
            tvUserName.setText(currentStudent.getHoTen());
            tvFullName.setText(currentStudent.getHoTen());
            tvEmail.setText(currentStudent.getEmail());
            tvStudentId.setText(currentStudent.getMaSV());
            tvSchool.setText(currentStudent.getTruong());
            tvMajor.setText(currentStudent.getNganh());
        }

        // ===================== XỬ LÝ HEADER =====================
        btnQuaylai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại màn trước (thường là Trang chủ)
                finish();
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_profile.this, activity_setting.class);
                // Nếu Setting cũng cần thông tin học sinh thì truyền thêm:
                // intent.putExtra("hoc_sinh", currentStudent);
                startActivity(intent);
            }
        });

        // ===================== BOTTOM MENU =====================
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_profile.this, activity_trangchu.class);
                intent.putExtra("hoc_sinh", currentStudent);
                startActivity(intent);
            }
        });

        navLichhoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_profile.this, activity_lichhoc.class);
                // Nếu cần biết sinh viên nào để lọc lịch thì cũng putExtra giống trên
                // intent.putExtra("hoc_sinh", currentStudent);
                startActivity(intent);
            }
        });

        navBamgio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_profile.this, activity_bamgio.class);
                startActivity(intent);
            }
        });

        navNhiemvu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_profile.this, activity_nhiemvu.class);
                startActivity(intent);
            }
        });
    }
}
