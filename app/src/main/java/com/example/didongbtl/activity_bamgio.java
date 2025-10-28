package com.example.didongbtl;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

public class activity_bamgio extends AppCompatActivity {

    Button btnStart , btnPause , btnReset , btnThemviec ;
    RecyclerView rvListcongviec ;
    private LinearLayout navHome, navLichhoc, navBamgio, navNhiemvu;
    private ImageButton btnSetting , btnQuaylai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bamgio);

        btnStart = findViewById(R.id.btnStart );
        btnPause = findViewById(R.id.btnPause);
        btnReset = findViewById(R.id.btnReset);
        btnThemviec = findViewById(R.id.btnThemviec);
        rvListcongviec = findViewById(R.id.rvListcongviec);

        btnThemviec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
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
                Intent intent =  new Intent(activity_bamgio.this , activity_trangchu.class);
                finish();
            }
        });
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_bamgio.this , activity_setting.class) ;
                startActivity(intent);
            }
        });

        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_bamgio.this, activity_trangchu.class);
                startActivity(intent);
            }
        });
        navLichhoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_bamgio.this, activity_lichhoc.class);
                startActivity(intent);
            }
        });
        navBamgio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_bamgio.this, activity_bamgio.class);
                startActivity(intent);
            }
        });
        navNhiemvu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_bamgio.this, activity_nhiemvu.class);
                startActivity(intent);
            }
        });
        // ======================================================================================================================//

    }
    private void showAddDialog() {
        // 1. Khởi tạo Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        // 2. Gán layout tùy chỉnh cho Dialog
        View dialogView = inflater.inflate(R.layout.dialog_thembamgio, null);
        builder.setView(dialogView);

        // 3. Ánh xạ các EditText
        final EditText edtTencongviec = dialogView.findViewById(R.id.edtTencongviec);
        final EditText edtThoigian = dialogView.findViewById(R.id.edtThoigian);

        // 4. Thiết lập các nút hành động
        builder.setPositiveButton("LƯU", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String name = edtTencongviec.getText().toString().trim();
                String time = edtThoigian.getText().toString().trim();

                if (!name.isEmpty() && !time.isEmpty()) {
                    // TODO: 5. Xử lý lưu công việc và cập nhật RecyclerView
                    Toast.makeText(activity_bamgio.this, "Đã thêm: " + name + " - " + time, Toast.LENGTH_SHORT).show();
                    // Thêm đối tượng Task mới vào List<Task> và gọi adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(activity_bamgio.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        // 6. Hiển thị Dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}