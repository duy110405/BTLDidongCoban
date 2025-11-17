package com.example.didongbtl;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class activity_addnhiemvu extends AppCompatActivity {

    private LinearLayout navHome, navLichhoc, navBamgio, navNhiemvu;
    private ImageButton btnSetting, btnQuaylai;

    private TextView txtChonngay, txtChongio, btnLuuNhiemvu;
    private EditText edtTieude, edtMota;

    private String selectedDate = "";
    private String selectedTime = "";
    private String mucDoUuTien = "TRUNG BÌNH"; // tạm default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_addnhiemvu);

        // Ánh xạ view
        txtChonngay = findViewById(R.id.txtChonngay);
        txtChongio = findViewById(R.id.txtChongio);
        btnLuuNhiemvu = findViewById(R.id.btnLuuNhiemvu);
        edtTieude = findViewById(R.id.edtTieudeNhiemvu);
        edtMota = findViewById(R.id.edtMotaNhiemvu);

        navHome = findViewById(R.id.navHome);
        navLichhoc = findViewById(R.id.navLichhoc);
        navBamgio = findViewById(R.id.navBamgio);
        navNhiemvu = findViewById(R.id.navNhiemvu);
        btnSetting = findViewById(R.id.btnSetting);
        btnQuaylai = findViewById(R.id.btnQuaylai);

        // back
        btnQuaylai.setOnClickListener(v -> finish());

        btnSetting.setOnClickListener(v -> {
            Intent intent = new Intent(activity_addnhiemvu.this, activity_setting.class);
            startActivity(intent);
        });

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(activity_addnhiemvu.this, activity_trangchu.class);
            startActivity(intent);
        });

        navLichhoc.setOnClickListener(v -> {
            Intent intent = new Intent(activity_addnhiemvu.this, activity_lichhoc.class);
            startActivity(intent);
        });

        navBamgio.setOnClickListener(v -> {
            Intent intent = new Intent(activity_addnhiemvu.this, activity_bamgio.class);
            startActivity(intent);
        });

        navNhiemvu.setOnClickListener(v -> {
            Intent intent = new Intent(activity_addnhiemvu.this, activity_nhiemvu.class);
            startActivity(intent);
        });

        // chọn ngày / giờ
        txtChonngay.setOnClickListener(v -> showDatePickerDialog());
        txtChongio.setOnClickListener(v -> showTimePickerDialog());

        // Lưu nhiệm vụ
        btnLuuNhiemvu.setOnClickListener(v -> {
            String tieuDe = edtTieude.getText().toString().trim();
            String moTa = edtMota.getText().toString().trim();

            if (tieuDe.isEmpty()) {
                edtTieude.setError("Nhập tiêu đề nhiệm vụ");
                edtTieude.requestFocus();
                return;
            }

            Intent result = new Intent();
            result.putExtra("tenTask", tieuDe);
            result.putExtra("moTa", moTa);
            result.putExtra("gioHetHan", selectedTime);
            result.putExtra("ngayHetHan", selectedDate);
            result.putExtra("mucDo", mucDoUuTien);

            setResult(RESULT_OK, result);
            finish();
        });
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, y, m, d) -> {
                    selectedDate = String.format(Locale.getDefault(),
                            "%02d/%02d/%d", d, m + 1, y);
                    txtChonngay.setText("📆 " + selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, h, m) -> {
                    selectedTime = String.format(Locale.getDefault(),
                            "%02d:%02d", h, m);
                    txtChongio.setText("⏰ " + selectedTime);
                },
                hour, minute, true
        );
        timePickerDialog.show();
    }
}
