package com.example.didongbtl;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class activity_setting extends AppCompatActivity {

    ImageButton btnQuaylai;
    LinearLayout layoutlogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);

        btnQuaylai  = findViewById(R.id.btnQuaylai);
        layoutlogout = findViewById(R.id.layoutLogout);

        // back về trang chủ
        btnQuaylai.setOnClickListener(v -> {
            Intent intent = new Intent(activity_setting.this, activity_trangchu.class);
            startActivity(intent);
            finish();
        });

        // Đăng xuất
        layoutlogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất khỏi tài khoản này không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    // 1. Xoá user đang đăng nhập
                    SessionManager.clearUser(this);

                    // 2. Về màn đăng nhập, xoá luôn back stack
                    Intent intent = new Intent(activity_setting.this, activity_signin.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
